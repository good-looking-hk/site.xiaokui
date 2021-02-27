package site.xiaokui.task;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.UserService;
import org.beetl.sql.core.SQLReady;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import site.xiaokui.Constants;
import site.xiaokui.constant.RedisKey;
import site.xiaokui.domain.SysBlog;
import site.xiaokui.service.EmailService;
import site.xiaokui.service.RedisService;
import site.xiaokui.service.impl.SysBlogServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
 * 0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时
 * 0 0 12 ? * WED 表示每个星期三中午12点
 * "0 0 12 * * ?" 每天中午12点触发
 * "0 15 10 ? * *" 每天上午10:15触发
 * "0 15 10 * * ?" 每天上午10:15触发
 * "0 15 10 * * ? *" 每天上午10:15触发
 * "0 15 10 * * ? 2005" 2005年的每天上午10:15触发
 * "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
 * "0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发
 * "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
 * "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
 * "0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发
 * "0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发
 * "0 15 10 15 * ?" 每月15日上午10:15触发
 * "0 15 10 L * ?" 每月最后一日的上午10:15触发
 * "0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
 * "0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发
 * "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发
 *
 * @author HK
 * @date 2021-02-25 14:44
 */
@Slf4j
@Component("BlogViewCountTask")
@RequiredArgsConstructor
public class BlogViewCountTask {

    private final RedisService redisService;

    private final SysBlogServiceImpl sysBlogServiceImpl;

    private final UserService userService;

    @Value("${spring.profiles.active}")
    private String profile;

    /**
     * 同步数据库访问量到redis缓存
     */
    public void syncDbViewCountToRedis() {
        // 将数据库数据刷到缓存
        List<User> list = userService.all();
        for (User user : list) {
            sysBlogServiceImpl.setMostViewCache(user.getId());
        }
    }

    /**
     * 同步Redis博客访问量到数据库
     */
    public void syncRedisViewCountToDb() {
        try (Jedis jedis = redisService.getRedis()) {
            long start = System.nanoTime();
            int count = 0;
            // 某篇博客的阅读贡献量
            Set<String> keySet = jedis.keys("*" + RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX);
            if (keySet == null || keySet.size() == 0) {
                return;
            }
            for (String key : keySet) {
                // redis记录更新至数据库
                Set<Tuple> sets = jedis.zrangeByScoreWithScores(key, "-inf", "+inf");
                for (Tuple t : sets) {
                    SysBlog blog = new SysBlog();
                    blog.setId(Long.valueOf(t.getElement()));
                    double score = t.getScore();
                    if (score < 1) {
                        continue;
                    }
                    count++;
                    blog.setViewCount((int) score);
                    sysBlogServiceImpl.getSqlManager().executeUpdate(new SQLReady(
                            "update sys_blog set yesterday_view = ? - view_count, view_count = ? where id = ?", score, score, blog.getId()
                    ));
                }
            }
            log.info("redis博客阅读量记录更新至数据库耗时{}ms，记录为{}条", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start), count);
        } catch (Exception e) {
            if (Constants.PROFILE_REMOTE.equals(profile)) {
                String msg = ExceptionUtil.stacktraceToString(e);
                log.error("redis任务执行失败，异常信息如下：\n" + msg);
                EmailService.sendError(msg);
            } else {
                log.error("redis任务执行失败，异常信息如下：\n" + ExceptionUtil.stacktraceToString(e));
            }
        }
    }

    /**
     * redis清除博客贡献访问量黑名单
     */
    public void clearContributeBlackList() {
        log.info("执行redis任务:清空博客贡献黑名单");
        long start = System.currentTimeMillis();
        try (Jedis jedis = redisService.getRedis()) {
            StringBuilder sb = new StringBuilder();
            // 用户或IP总贡献访问量
            Map<String, String> map = jedis.hgetAll(RedisKey.USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP);
            long total = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append("user_id/ip:").append(entry.getKey()).append(" contribute ").append(entry.getValue()).append(";\n");
                total += Integer.parseInt(entry.getValue());
            }
            // 清除ip阅读贡献量黑名单，返回删除条数，默认为1
            jedis.del(RedisKey.USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP);
            sb.append("清除用户/IP对所有博客的阅读贡献量").append(total).append("条;\n");

            // 清除用户或IP对某篇博客的阅读贡献量
            Set<String> strs = jedis.keys("*" + RedisKey.USER_OR_IP_TO_BLOG_CONTRIBUTE_VIEW_COUNT_SUFFIX);
            if (strs != null && strs.size() > 0) {
                total = jedis.del(strs.toArray(new String[0]));
            }
            sb.append("清除用户/IP对单篇博客的阅读贡献量").append(total).append("条;\n");
            log.info(sb.toString());
        } catch (Exception e) {
            if (Constants.PROFILE_REMOTE.equals(profile)) {
                String msg = ExceptionUtil.stacktraceToString(e);
                log.error("redis任务执行失败，异常信息如下：\n" + msg);
                EmailService.sendError(msg);
            } else {
                log.error("redis任务执行失败，异常信息如下：\n" + ExceptionUtil.stacktraceToString(e));
            }
        }
    }

    /**
     * 删除redis中所有与博客阅读量相关的缓存key
     */
    public void dealAllBlogReadsRedisKey() {
        try (Jedis jedis = redisService.getRedis()) {
            // 清除ip阅读贡献量黑名单，返回删除条数，默认为1
            jedis.del(RedisKey.USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP);

            // 清除某用户所有博客的阅读贡献量
            jedis.del("*" + RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX);

            // 清除用户或IP对某篇博客的阅读贡献量限制
            jedis.del("*" + RedisKey.USER_OR_IP_TO_BLOG_CONTRIBUTE_VIEW_COUNT_SUFFIX);
        } catch (Exception e) {
            log.error("清除redis缓存失败", e);
            e.printStackTrace();
        }
    }
}
