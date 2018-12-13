package site.xiaokui.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import site.xiaokui.common.util.SerializeUtil;

import java.io.Serializable;

/**
 * @author HK
 * @date 2018-10-06 21:25
 */
@Slf4j
@Repository
public class RedisDao {

    private final JedisPool jedisPool;

    private static final String PREFIX = "~123$543#", SUFFIX = "*098@890";

    /**
     * 缓存获取时间，默认1小时
     */
    private static final int TIME_OUT_SEC = 60 * 60;

    public RedisDao(@Value("${spring.redis.host}") String ip, @Value("${spring.redis.port}") int port) {
        jedisPool = new JedisPool(ip, port);
    }

    public String put(Serializable o, String id) {
        return put(o, id, false);
    }

    public String put(Serializable o, Integer id) {
        return put(o, String.valueOf(id), false);
    }

    public <T> T get(String id, Class<T> cls) {
        return get(id, false, cls);
    }

    public <T> T get(Integer id, Class<T> cls) {
        return get(String.valueOf(id), false, cls);
    }

    public String put(Serializable o, String id, boolean needSalt) {
        Jedis jedis = jedisPool.getResource();
        try {
            String key = !needSalt ? id : PREFIX + id + SUFFIX;
            byte[] bytes = SerializeUtil.serialize(o);
            log.debug("将对象放入Redis缓存（{}）", o);
            return jedis.setex(key.getBytes(), TIME_OUT_SEC, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    public <T> T get(String id, boolean needSalt, Class<T> cls) {
        Jedis jedis = jedisPool.getResource();
        try {
            String key = !needSalt ? id : PREFIX + id + SUFFIX;
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) {
                T t = SerializeUtil.deserialize(bytes, cls);
                log.debug("从Redis缓存获取数据({})", t);
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    public void remove(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
    }
}
