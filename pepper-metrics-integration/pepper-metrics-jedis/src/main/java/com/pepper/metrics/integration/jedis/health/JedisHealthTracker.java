package com.pepper.metrics.integration.jedis.health;

import com.pepper.metrics.core.HealthTracker;
import com.pepper.metrics.core.MetricsRegistry;
import org.junit.Assert;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.PjedisPool;
import redis.clients.util.Pool;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-12
 */
public class JedisHealthTracker {
    private static Set<String> UNIQUE_NAME = new ConcurrentSkipListSet<>();

    public static void addJedisPool(JedisPool jedisPool) {
        addPool("default", jedisPool);
    }

    public static void addJedisPool(String namespace, JedisPool jedisPool) {
        addPool(namespace, jedisPool);
    }

    public static void addJedisPool(PjedisPool jedisPool) {
        addPool("default", jedisPool);
    }

    public static void addJedisPool(String namespace, PjedisPool jedisPool) {
        addPool(namespace, jedisPool);
    }

    private static void addPool(String namespace, Pool jedisPool) {
        Assert.assertNotNull(namespace);
        Assert.assertFalse("Duplicate namespace error.", UNIQUE_NAME.contains(namespace));
        UNIQUE_NAME.add(namespace);
        final JedisHealthStats stats = new JedisHealthStats(MetricsRegistry.getREGISTRY(), namespace, jedisPool);
        HealthTracker.addStats(stats);
    }

}
