package site.xiaokui.util.useful;

import redis.clients.jedis.Jedis;
import site.xiaokui.constant.RedisKey;
import site.xiaokui.service.RedisService;

/**
 * redis 博客阅读量查看工具
 * @author HK
 * @date 2021-02-25 14:24
 */
public class RedisUtil {

    public static void main(String[] args) {
        RedisService redisService = new RedisService("127.0.0.1", 6379, "");
        Jedis jedis = redisService.getRedis();

    }
}
