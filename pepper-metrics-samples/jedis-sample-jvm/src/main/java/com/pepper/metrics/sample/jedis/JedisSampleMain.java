package com.pepper.metrics.sample.jedis;

import com.pepper.metrics.integration.jedis.health.JedisHealthTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPropsHolder;
import redis.clients.jedis.PjedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;

public class JedisSampleMain {
    private static final Logger log = LoggerFactory.getLogger(JedisSampleMain.class);
    public static void main(String[] args) {
        try {

            testJedis();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testJedis() throws InterruptedException {
        log.info("testJedis()");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(300);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setMaxWaitMillis(6000);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        config.setTestOnCreate(false);

        log.info("init JedisPoolConfig: {}", config.toString());
        final String namespace = "myns";
        JedisPropsHolder.NAMESPACE.set(namespace);
        PjedisPool jedisPool = new PjedisPool(config, "192.168.100.221", 6379);
        JedisHealthTracker.addJedisPool(namespace, jedisPool);
        for (int j = 0; j < 100; j++) {
            for (int i = 0; i < 10; i++) {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.set("hello", "robin");
                }
            }
            log.info(String.format("%s NumActive:%s NumIdle:%s", j, jedisPool.getNumActive(), jedisPool.getNumIdle()));
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
