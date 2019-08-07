package com.pepper.metrics.sample.jedis;

import redis.clients.jedis.PjedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;

public class MAIN {
    public static void main(String[] args) {
        try {
            testJedis();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void testJedis() throws InterruptedException {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(300);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setMaxWaitMillis(6000);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        config.setTestOnCreate(false);

        PjedisPool jedisPool = new PjedisPool(config, "192.168.100.221", 6379, "somens");

        for (int j = 0; j < 100; j++) {
            for (int i = 0; i < 10; i++) {
                final Jedis resource = jedisPool.getResource();
                resource.set("hello", "robin");
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
