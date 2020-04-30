package site.xiaokui.module.base.service;

import redis.clients.jedis.Jedis;

/**
 * 暂时没发现这样做有什么问题，使用JedisPool的话，代码过于臃肿
 * @author HK
 * @date 2019-02-20 16:58
 */
public class RedisTool {

    /**
     * 内部类单例，按需加载
     */
    private static class RedisInstance1 {
        private static Jedis jedis = new redis.clients.jedis.Jedis("localhost");
        static {
            System.out.println("RedisTool============连接到Redis" + jedis.toString());
        }
    }

    public static Jedis getRedis() {
        return RedisInstance1.jedis;
    }
}
