package com.pepper.metrics.integration.jedis.health;

import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.utils.MetricsNameBuilder;
import com.pepper.metrics.core.utils.MetricsType;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.PjedisPool;
import redis.clients.util.Pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-12
 */
public class JedisHealthStats extends HealthStats {
    private Pool pool;

    private final Map<String, AtomicLong> gaugeCollector = new ConcurrentHashMap<>();

    private final Map<String, String> constantsCollector = new ConcurrentHashMap<>();

    public Map<String, AtomicLong> getGaugeCollector() {
        return gaugeCollector;
    }

    public Map<String, String> getConstantsCollector() {
        return constantsCollector;
    }

    public JedisHealthStats(MeterRegistry registry, String namespace, Pool pool) {
        super(registry, namespace);
        this.pool = pool;
    }

    public void gaugeCollect(String gaugeName, long value) {
        getOrInitGauge(gaugeName).set(value);
    }

    public Pool getPool() {
        return pool;
    }

    public void constantsCollect(String gaugeName, String value) {
        constantsCollector.put(gaugeName, value);
    }

    private AtomicLong getOrInitGauge(String gaugeName) {
        String[] tags = {"GaugeName", gaugeName, "namespace", getNamespace()};
        final AtomicLong gauge = gaugeCollector.get(gaugeName);
        if (gauge != null) return gauge;
        synchronized (gaugeCollector) {
            if (gaugeCollector.get(gaugeName) == null) {
                final AtomicLong obj = new AtomicLong();

                String metricsName = MetricsNameBuilder.builder()
                        .setMetricsType(MetricsType.GAUGE)
                        .setType("jedis")
                        .setSubType("default")
                        .setName(gaugeName)
                        .build();
                Gauge.builder(metricsName, obj, AtomicLong::get).tags(tags).register(getRegistry());
                gaugeCollector.putIfAbsent(gaugeName, obj);
            }
        }
        return gaugeCollector.get(gaugeName);
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
    }
}
