package com.pepper.metrics.integration.jedis.health;

import com.pepper.metrics.core.HealthTracker;
import com.pepper.metrics.core.MetricsRegistry;
import org.junit.Assert;
import redis.clients.jedis.JedisCluster;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-16
 */
public class JedisClusterHealthTracker {

    private static Set<String> UNIQUE_NAME = new ConcurrentSkipListSet<>();

    public static void addJedisCluster(String namespace, JedisCluster jedisCluster) {
        Assert.assertNotNull(namespace);
        Assert.assertFalse("Duplicate namespace error.", UNIQUE_NAME.contains(namespace));
        UNIQUE_NAME.add(namespace);
        JedisClusterHealthStats stats = new JedisClusterHealthStats(MetricsRegistry.getREGISTRY(), namespace, jedisCluster);
        HealthTracker.addStats(stats);
    }
}
