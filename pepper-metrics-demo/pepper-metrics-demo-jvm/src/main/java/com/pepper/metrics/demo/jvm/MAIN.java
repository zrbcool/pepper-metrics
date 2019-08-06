package com.pepper.metrics.demo.jvm;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.integration.jedis.PjedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class MAIN {
    public static void main(String[] args) throws InterruptedException {
        testJedis();
        final Stats stats = Profiler.Builder.builder().name("http.in").namespace("default").build();
        String[] tags = new String[]{"url", "/api/news1"};
        long begin = System.currentTimeMillis();
        stats.incConc(tags);
        TimeUnit.SECONDS.sleep(1);
        stats.decConc(tags);
        stats.error(tags);
        stats.observe(System.currentTimeMillis() - begin);

        System.out.println("done");


        TimeUnit.SECONDS.sleep(101);
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

        PjedisPool jedisPool = new PjedisPool(config, "192.168.100.221", 6379);

        for (int j = 0; j < 100; j++) {
            for (int i = 0; i < 10; i++) {
                final Jedis resource = jedisPool.getResource();
                resource.set("hello", "robin");
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
