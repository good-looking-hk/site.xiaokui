package site.xiaokui.module.sys.qq.util;

import redis.clients.jedis.Jedis;
import site.xiaokui.module.sys.qq.entity.QqStatus;

/**
 * @author HK
 * @date 2019-02-14 13:24
 */
public class RedisUtil {

    private static final String SUFFIX_STATUS = "_status";

    private static final int ONE_HOUR = 60 * 60;

    private static final String REPLAY_OK = "OK";

    /**
     * 内部类单例，按需加载
     */
    private static class RedisInstance {
        private static Jedis jedis = new Jedis("localhost");
    }

    private Jedis getRedis() {
        return RedisInstance.jedis;
    }

    /**
     * 更新状态，暂定为在线，离线，隐身
     */
    public boolean setStatus(Integer userId, String status) {
        Jedis jedis = getRedis();
        if (status.equals(QqStatus.ONLINE.getMsg()) || status.equals(QqStatus.HIDE.getMsg()) ) {
            String r = jedis.setex(userId + SUFFIX_STATUS, ONE_HOUR, status);
            if (r.equals(REPLAY_OK)) {
                return true;
            }
            return false;
        } else {
            jedis.del(userId + SUFFIX_STATUS);
        }
        return true;
    }

    /**
     * 如果不存在则返回null
     */
    public String getStatus(Integer userId) {
        Jedis jedis = getRedis();
        return jedis.get(userId + SUFFIX_STATUS);
    }

    private RedisUtil() {
    }

    public static void main(String[] args) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setStatus(1, "online");
        System.out.println(redisUtil.getStatus(2));
    }
}
