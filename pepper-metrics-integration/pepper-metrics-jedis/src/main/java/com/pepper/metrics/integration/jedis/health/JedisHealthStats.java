package com.pepper.metrics.integration.jedis.health;

import com.pepper.metrics.core.HealthStatsDefault;
import io.micrometer.core.instrument.MeterRegistry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.PjedisPool;
import redis.clients.util.Pool;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-12
 */
public class JedisHealthStats extends HealthStatsDefault {
    private Pool pool;

    public JedisHealthStats(MeterRegistry registry, String namespace, Pool pool) {
        super(registry, namespace);
        this.pool = pool;
    }

    public Pool getPool() {
        return pool;
    }

    public void collectStats() {
        if (pool instanceof PjedisPool) {
            try (Jedis jedis = ((PjedisPool)pool).getResource()) {
                constantsCollect("Host", jedis.getClient().getHost());
                constantsCollect("Port", jedis.getClient().getPort() + "");
            }
        }

        if (pool instanceof JedisPool) {
            try (Jedis jedis = ((JedisPool)pool).getResource()) {
                constantsCollect("Host", jedis.getClient().getHost());
                constantsCollect("Port", jedis.getClient().getPort() + "");
            }
        }
        gaugeCollect("NumActive", pool.getNumActive());
        gaugeCollect("NumIdle", pool.getNumIdle());
        gaugeCollect("NumWaiters", pool.getNumWaiters());
        gaugeCollect("MaxBorrowWaitTimeMillis", pool.getMaxBorrowWaitTimeMillis());
        gaugeCollect("MeanBorrowWaitTimeMillis", pool.getMeanBorrowWaitTimeMillis());
        infoCollect();
    }

    @Override
    public String getType() {
        return "jedis";
    }

    @Override
    public String getSubType() {
        return "default";
    }
}
