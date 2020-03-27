package site.xiaokui.module.sys.blog.service;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import site.xiaokui.module.base.service.RedisService;
import site.xiaokui.module.sys.blog.RedisKey;
import site.xiaokui.module.sys.blog.entity.SysBlog;

import java.util.*;


/**
 * 管理博客的相关缓存
 * @author HK
 * @date 2019-12-18 11:18
 */
@Slf4j
@Service
public class BlogCacheService {

    @Autowired
    private RedisService redisService;

    @Value("${xiaokui.most-view}")
    private Integer mostView;

    public int getViewCount(Integer blogId) {
        Jedis jedis = redisService.getRedis();
        try {
            String result = jedis.hget(RedisKey.ALL_HASH_BLOG_VIEW_COUNT, blogId.toString());
            if (result == null) {
                return 1;
            }
            int views = Integer.parseInt(result);
            return views + 1;
        } catch (Exception e) {
            log.error("redis操作错误", e);
            throw e;
        } finally {
            jedis.close();
        }
    }

    public void clearCache(Integer userId) {

    }

    /**
     * 设置最多缓存
     */
    public Map<String, Double> setMostView(Integer userId, List<SysBlog> blogs) {
        if (blogs == null || blogs.size() == 0) {
            return Collections.emptyMap();
        }
        // 存入map再存入redis
        Map<String, Double> map = new HashMap<>(64);
        for (SysBlog b : blogs) {
            if (b.getViewCount() == null) {
                map.put(String.valueOf(b.getId()), 0.0);
            } else {
                map.put(String.valueOf(b.getId()), b.getViewCount().doubleValue());
            }
        }

        Jedis jedis = redisService.getRedis();
        try {
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                // 所有博客的访问量
                jedis.hincrBy(RedisKey.ALL_HASH_BLOG_VIEW_COUNT, entry.getKey(), entry.getValue().longValue());
            }
            jedis.zadd(userId + RedisKey.KEY_MOST_VIEW_SUFFIX, map);
            Set<Tuple> sets = jedis.zrevrangeByScoreWithScores(userId + RedisKey.KEY_MOST_VIEW_SUFFIX,
                    "+inf", "-inf", 1, mostView);
            map = new LinkedHashMap<>(mostView);
            for (Tuple p : sets) {
                map.put(p.getElement(), p.getScore());
            }
            log.info("从数据库读取用户{}的博客id-访问量列表，并存至redis缓存，记录为{}条", userId, blogs.size());
            return map;
        } catch (Exception e) {
            log.error("redis读取userId:{}博客top{}出错", userId, mostView);
            throw e;
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取最多访问缓存
     */
    public Map<String, Double> getMostView(Integer userId) {
        Jedis jedis = redisService.getRedis();
        try {
            // 取top10，不能用Double.MAX_VALUE或Double.MIN_VALUE
            Set<Tuple> sets = jedis.zrevrangeByScoreWithScores(userId + RedisKey.KEY_MOST_VIEW_SUFFIX,
                    "+inf", "-inf", 0, mostView);
            if (sets == null || sets.size() == 0) {
                return Collections.emptyMap();
            }
            Map<String, Double>  map = new LinkedHashMap<>(mostView);
            for (Tuple p : sets) {
                map.put(p.getElement(), p.getScore());
            }
            log.info("从数据库读取用户{}的博客id-访问量列表，并存至redis缓存，记录为{}条", userId, map.size());
            return map;
        } catch (Exception e) {
            log.error("redis读取userId:{}博客top {}出错", userId, mostView);
            throw e;
        } finally {
            jedis.close();
        }
    }

    /**
     * 一个ip对于一篇博客，不登录一天最多贡献2个访问量，登录最多贡献4个访问量，
     * 不登录每天至多贡献40个阅读量，登录后最多贡献80个阅读量
     * 每晚12点整清空缓存存入数据库，Redis设计如下
     * Hash键：blogId + 后缀，Field成员：ip，Value值：访问次数
     * Hash键：BLACK_VIEW_IP，Field成员：userId/ip，Value值：总贡献阅读量
     * 阅读量每天23：10更新至数据库
     * TODO 可配置化？
     */
    public void addViewCount(String ip, Integer userId, Integer blogId, Integer onwerId) {
        Jedis jedis = redisService.getRedis();
        try {
            if (userId == null) {
                // 是否还有贡献阅读量能力
                String sum = jedis.hget(RedisKey.KEY_BLACK_VIEW_IP, ip);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= 40) {
                        return;
                    }
                }
                // 是否还能为此篇博客贡献阅读量
                sum = jedis.hget(blogId + RedisKey.HASH_IP_VIEWS_SUFFIX, ip);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= 2) {
                        return;
                    }
                }

                // 读者有能力贡献阅读量，更新数据，没有设置缓存过期时间，需要依赖定时任务统一时间清除缓存，下同
                // 记录ip的阅读总贡献量
                jedis.hincrBy(RedisKey.KEY_BLACK_VIEW_IP, ip, 1);
                // 记录ip对该博客的的阅读贡献量
                jedis.hincrBy(blogId + RedisKey.HASH_IP_VIEWS_SUFFIX, ip, 1);
                // 记录博客的总阅读量
                jedis.hincrBy(RedisKey.ALL_HASH_BLOG_VIEW_COUNT, String.valueOf(blogId), 1L);
                // 记录用户博客的阅读量
                jedis.zincrby(onwerId + RedisKey.KEY_MOST_VIEW_SUFFIX, 1.0, String.valueOf(blogId));
                log.debug("ip({})为博客({})贡献一个阅读量", ip, blogId);
            } else {
                String id = String.valueOf(userId);
                String sum = jedis.hget(RedisKey.KEY_BLACK_VIEW_IP, id);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= 80) {
                        return;
                    }
                }
                sum = jedis.hget(blogId + RedisKey.HASH_IP_VIEWS_SUFFIX, id);
                if (NumberUtil.isInteger(sum)) {
                    int s = Integer.parseInt(sum);
                    if (s >= 4) {
                        return;
                    }
                }
                // 记录ip的阅读总贡献量
                jedis.hincrBy(RedisKey.KEY_BLACK_VIEW_IP, id, 1);
                // 记录ip对该博客的的阅读贡献量
                jedis.hincrBy(blogId + RedisKey.HASH_IP_VIEWS_SUFFIX, id, 1);
                // 记录博客的总阅读量
                jedis.hincrBy(RedisKey.ALL_HASH_BLOG_VIEW_COUNT, String.valueOf(blogId), 1L);
                // 记录用户博客的阅读量
                jedis.zincrby(onwerId + RedisKey.KEY_MOST_VIEW_SUFFIX, 1.0, String.valueOf(blogId));
                log.debug("用户({})为博客({})贡献一个阅读量", id, blogId);
            }
        } catch (Exception e) {
            log.error("redis添加访问量时出错ip={},userId={},blogId={},error={}", ip, userId, blogId, e.getMessage());
            throw e;
        } finally {
            jedis.close();
        }
    }
}
