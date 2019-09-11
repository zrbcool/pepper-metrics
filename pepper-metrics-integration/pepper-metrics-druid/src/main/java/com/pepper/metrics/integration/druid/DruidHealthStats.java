package com.pepper.metrics.integration.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.utils.MetricsNameBuilder;
import com.pepper.metrics.core.utils.MetricsType;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class DruidHealthStats extends HealthStats {

    private final Map<String, AtomicLong> gaugeCollector = new ConcurrentHashMap<>();

    private final Map<String, String> constantsCollector = new ConcurrentHashMap<>();

    public Map<String, AtomicLong> getGaugeCollector() {
        return gaugeCollector;
    }

    public Map<String, String> getConstantsCollector() {
        return constantsCollector;
    }

    private DruidDataSource druidDataSource;

    public DruidHealthStats(MeterRegistry registry, String namespace, DruidDataSource druidDataSource) {
        super(registry, namespace);
        this.druidDataSource = druidDataSource;
    }

    public DruidDataSource getDruidDataSource() {
        return druidDataSource;
    }

    public void gaugeCollect(String gaugeName, long value) {
        getOrInitGauge(gaugeName).set(value);
    }

    public void constantsCollect(String gaugeName, String value) {
        constantsCollector.put(gaugeName, value);
    }

    private AtomicLong getOrInitGauge(String gaugeName) {
        String[] tags = {"GaugeName", gaugeName, "DataSourceName", druidDataSource.getName()};
        final AtomicLong gauge = gaugeCollector.get(gaugeName);
        if (gauge != null) return gauge;
        synchronized (gaugeCollector) {
            if (gaugeCollector.get(gaugeName) == null) {
                final AtomicLong obj = new AtomicLong();

                String metricsName = MetricsNameBuilder.builder()
                        .setMetricsType(MetricsType.GAUGE)
                        .setType("druid")
                        .setSubType("default")
                        .setName(gaugeName)
                        .build();
                Gauge.builder(metricsName, obj, AtomicLong::get).tags(tags).register(getRegistry());
                gaugeCollector.putIfAbsent(gaugeName, obj);
            }
        }
        return gaugeCollector.get(gaugeName);
    }

}
