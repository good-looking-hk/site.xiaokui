package site.xiaokui.module.base.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;
import site.xiaokui.common.util.SerializeUtil;

/**
 * 使系统支持Redis支持普通K/V存储
 *
 * @author HK
 * @date 2018-10-06 21:25
 */
@Slf4j
@Repository
public class RedisService {

    private final JedisPool jedisPool;

    public static final int ONE_HOUR = 60 * 60, ONE_DAY = 24 * ONE_HOUR, ONE_WEEK = 7 * ONE_DAY, ONE_MONTH = 30 * ONE_DAY;

    public RedisService(@Value("${spring.redis.host}") String ip, @Value("${spring.redis.port}") int port, @Value("${spring.redis.password}") String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大空闲连接数, 默认8个
        config.setMaxIdle(16);
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(8000);
        // 最大连接数, 默认8个
        config.setMaxTotal(16);

        if (password == null || "".equals(password)) {
            jedisPool = new JedisPool(config, ip, port);
        } else {
            jedisPool = new JedisPool(config, ip, port, Protocol.DEFAULT_TIMEOUT, password,
                    Protocol.DEFAULT_DATABASE, null);
        }
        try {
            Jedis jedis = jedisPool.getResource();
            jedis.close();
        } catch (JedisException e) {
            log.error("【RedisService】--连接redis失败(localhost：{}，port：{}，password：{})", ip, port, password);
            throw e;
        }
        log.info("【RedisService】--已成功连接到redis(localhost：{}，port：{}，password：{})", ip, port, password);
    }

    /**
     * 虽然暴露了Jedis对象，但必须记得关闭
     */
    public Jedis getRedis() {
        return jedisPool.getResource();
    }

    public void set(String k, Object v, int secs) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] bytes = SerializeUtil.serialize(v);
            log.debug("将（{}）类型对象放入Redis缓存", v.getClass().getSimpleName());
            jedis.setex(k.getBytes(), secs, bytes);
        } catch (Exception e) {
            log.error("缓存失败[key:{},value:{}]", k, v);
            throw new RuntimeException("放入缓存失败：" + e.getMessage());
        } finally {
            jedis.close();
        }
    }

    public <T> T get(String k, Class<T> cls) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] bytes = jedis.get(k.getBytes());
            if (bytes != null) {
                T t = SerializeUtil.deserialize(bytes, cls);
                log.debug("从Redis缓存获取({})类型数据", t.getClass().getSimpleName());
                return t;
            }
        } catch (Exception e) {
            log.error("读取缓存失败[key:{}]", k);
            e.printStackTrace();
            throw new RuntimeException("读取缓存失败：" + e.getMessage());
        } finally {
            jedis.close();
        }
        return null;
    }

    public void remove(String... keys) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(keys);
        jedis.close();
    }
}
