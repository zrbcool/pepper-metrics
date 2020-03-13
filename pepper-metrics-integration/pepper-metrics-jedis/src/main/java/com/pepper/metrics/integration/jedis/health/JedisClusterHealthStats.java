package com.pepper.metrics.integration.jedis.health;

import com.google.common.collect.Maps;
import com.pepper.metrics.core.HealthStatsDefault;
import com.pepper.metrics.core.MetricsRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.collections4.MapUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

import java.util.Map;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-12
 */
public class JedisClusterHealthStats extends HealthStatsDefault {

    private JedisCluster jedisCluster;
    private Map<String, JedisClusterNodeHealthStats> nodeHealthStats = Maps.newConcurrentMap();

    public JedisClusterHealthStats(MeterRegistry registry, String namespace, JedisCluster jedisCluster) {
        super(registry, namespace);
        this.jedisCluster = jedisCluster;
    }

    @Override
    public String getType() {
        return "jedisCluster";
    }

    @Override
    public String getSubType() {
        return "default";
    }

    class JedisClusterNodeHealthStats extends HealthStatsDefault {
        private Pool pool;
        private String node;

        public JedisClusterNodeHealthStats(MeterRegistry registry, String namespace, String node, Pool pool) {
            super(registry, namespace);
            this.pool = pool;
            this.node = node;
        }

        public void collectStats() {
            String[] additionTags = {"node", node};
            if (pool instanceof JedisPool) {
                try (Jedis jedis = ((JedisPool)pool).getResource()) {
                    constantsCollect("Host", jedis.getClient().getHost());
                    constantsCollect("Port", jedis.getClient().getPort() + "");
                }
            }
            gaugeCollect("NumActive", pool.getNumActive(), additionTags);
            gaugeCollect("NumIdle", pool.getNumIdle(), additionTags);
            gaugeCollect("NumWaiters", pool.getNumWaiters(), additionTags);
            gaugeCollect("MaxBorrowWaitTimeMillis", pool.getMaxBorrowWaitTimeMillis(), additionTags);
            gaugeCollect("MeanBorrowWaitTimeMillis", pool.getMeanBorrowWaitTimeMillis(), additionTags);
            infoCollect();
        }

        @Override
        public String getType() {
            return "jedisCluster";
        }

        @Override
        public String getSubType() {
            return "default";
        }
    }

    public synchronized void collectStats() {
        nodeHealthStats.clear();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        if (MapUtils.isNotEmpty(clusterNodes)) {
            for (Map.Entry<String, JedisPool> poolEntry : clusterNodes.entrySet()) {
                JedisClusterNodeHealthStats stats = new JedisClusterNodeHealthStats(
                        MetricsRegistry.getREGISTRY(),
                        getNamespace(),
                        poolEntry.getKey(),
                        poolEntry.getValue()
                );
                nodeHealthStats.putIfAbsent(poolEntry.getKey(), stats);
            }
        }

        for (JedisClusterNodeHealthStats stats : nodeHealthStats.values()) {
            stats.collectStats();
        }
    }

    public Map<String, JedisClusterNodeHealthStats> getNodeHealthStats() {
        return nodeHealthStats;
    }
}
