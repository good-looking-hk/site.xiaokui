package site.xiaokui.service;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import site.xiaokui.RedisKey;
import site.xiaokui.domain.SysBlog;

import java.util.*;


/**
 * 管理博客的相关缓存
 *
 * @author HK
 * @date 2019-12-18 11:18
 */
@Slf4j
@Service
public class BlogCacheService {

    @Autowired
    private RedisService redisService;

    @Value("${xiaokui.single-ip-contribute-blog}")
    private Integer singleIpContributeBlog;

    @Value("${xiaokui.single-ip-contribute--all-blog}")
    private Integer singleIpContributeAllBlog;

    @Value("${xiaokui.single-user-contribute-blog}")
    private Integer singleUserContributeBlog;

    @Value("${xiaokui.single-user-contribute--all-blog}")
    private Integer singleUserContributeAllBlog;

    public int getViewCount(Long userId, Long blogId) {
        try (Jedis jedis = redisService.getRedis()) {
            // 对应redis里面有序集合，用户id键:博客id 访问数量
            String key = userId + RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX;
            String member = Long.toString(blogId);
            // 如果key不存在或member不存在，则返回null
            Double count = jedis.zscore(key, member);
            if (count == null) {
                jedis.zadd(key, 1.0, member);
                return 1;
            }
            // 包含本次访问
            return (int) (count + 1);
        } catch (Exception e) {
            log.error("redis操作错误", e);
            throw e;
        }
    }

    /**
     * 一个ip对于一篇博客，不登录一天最多贡献10个访问量，登录最多贡献20个访问量，
     * 不登录每天至多贡献40个阅读量，登录后最多贡献80个阅读量
     * 两个限制条件不冲突
     */
    public void addViewCount(String ip, Long userId, Long blogId, Long ownerId) {
        try (Jedis jedis = redisService.getRedis()) {
            if (userId == null) {
                // 是否还有贡献阅读量能力
                String sum = jedis.hget(RedisKey.USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP, ip);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= singleIpContributeAllBlog) {
                        return;
                    }
                }
                // 是否还能为此篇博客贡献阅读量
                sum = jedis.hget(blogId + RedisKey.USER_OR_IP_TO_BLOG_CONTRIBUTE_VIEW_COUNT_SUFFIX, ip);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= singleIpContributeBlog) {
                        return;
                    }
                }
                addViewCount(jedis, ip, ownerId, blogId);
                log.info("ip({})为博客({})贡献一个阅读量", ip, blogId);
            } else {
                String userId0 = String.valueOf(userId);
                String sum = jedis.hget(RedisKey.USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP, userId0);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= singleUserContributeAllBlog) {
                        return;
                    }
                }
                // 是否还能为此篇博客贡献阅读量
                sum = jedis.hget(blogId + RedisKey.USER_OR_IP_TO_BLOG_CONTRIBUTE_VIEW_COUNT_SUFFIX, userId0);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= singleUserContributeBlog) {
                        return;
                    }
                }
                addViewCount(jedis, userId0, ownerId, blogId);
                log.info("用户({})为博客({})贡献一个阅读量", userId0, blogId);
            }
        } catch (Exception e) {
            log.error("redis添加访问量时出错ip={},userId={},blogId={},error={}", ip, userId, blogId, e.getMessage());
            throw e;
        }
    }

    private void addViewCount(Jedis jedis, String userIdOrIp, Long ownerId, Long blogId) {
        // 记录ip的阅读总贡献量
        jedis.hincrBy(RedisKey.USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP, userIdOrIp, 1);
        // 记录ip对该博客的的阅读贡献量
        jedis.hincrBy(blogId + RedisKey.USER_OR_IP_TO_BLOG_CONTRIBUTE_VIEW_COUNT_SUFFIX, userIdOrIp, 1);
        // 增加用户博客的阅读量
        jedis.zincrby(ownerId + RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX, 1.0, String.valueOf(blogId));
    }

    /**
     * 获取用户最多访问缓存,返回该用户的TopN访问，博客id 访问量返回如
     * 1 10000
     * 2 5000
     * 3 200
     * ....
     */
    public LinkedHashMap<Long, Integer> getMostViewTopN(Long userId, int n) {
        try (Jedis jedis = redisService.getRedis()) {
            // 取top10，从大往小取，不能用Double.MAX_VALUE或Double.MIN_VALUE
            Set<Tuple> sets = jedis.zrevrangeByScoreWithScores(userId + RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX,
                    "+inf", "-inf", 0, n);
            if (sets == null || sets.size() == 0) {
                return new LinkedHashMap<>(0);
            }
            LinkedHashMap<Long, Integer> map = new LinkedHashMap<>(Math.min(n, sets.size()));
            for (Tuple p : sets) {
                map.put(Long.parseLong(p.getElement()), (int) p.getScore());
            }
            return map;
        } catch (Exception e) {
            log.error("redis操作错误", e);
            throw e;
        }
    }

    /**
     * 设置用户最多访问缓存
     */
    public void setMostView(Long userId, List<SysBlog> blogs) {
        if (blogs == null || blogs.size() == 0) {
            return;
        }
        try (Jedis jedis = redisService.getRedis()) {
            String key = userId + RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX;
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            // 存入map再批量导入redis
            Map<String, Double> map = new HashMap<>(blogs.size());
            for (SysBlog b : blogs) {
                if (b.getViewCount() == null) {
                    map.put(String.valueOf(b.getId()), 0.0);
                } else {
                    map.put(String.valueOf(b.getId()), b.getViewCount().doubleValue());
                }
            }
            jedis.zadd(key, map);
        } catch (Exception e) {
            log.error("redis操作错误", e);
            throw e;
        }
    }
}
