package site.xiaokui.util.useful;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import site.xiaokui.constant.RedisKey;
import site.xiaokui.domain.SysBlog;
import site.xiaokui.service.RedisService;

import java.util.Set;

/**
 * redis 博客阅读量查看工具
 *
 * @author HK
 * @date 2021-02-25 14:24
 */
@Slf4j
public class RedisUtil {

    public static void main(String[] args) {
        RedisService redisService = new RedisService("120.79.20.49", 6379, "1238127943Hk~");
//        RedisService redisService = new RedisService("127.0.0.1", 6379, "");
        try (Jedis jedis = redisService.getRedis()) {
            int count = 0;
            // 某篇博客的阅读贡献量
            Set<String> keySet = jedis.keys("*" + RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX);
            if (keySet == null || keySet.size() == 0) {
                return;
            }
            for (String key : keySet) {
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
                    log.info("博客阅读量信息: user_id={}  blog_id={}  reads={}", key.substring(0, key.length() - RedisKey.USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX.length()), blog.getId(), blog.getViewCount());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
