package com.pepper.metrics.integration.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.pepper.metrics.core.HealthStats;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 *
 * @author zhiminxu
 * @package com.pepper.metrics.integration.druid
 * @create_time 2019-09-09
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

    public DruidHealthStats(MeterRegistry registry, String name, String namespace, DruidDataSource druidDataSource) {
        super(registry, name, namespace);
        this.druidDataSource = druidDataSource;
    }

    public DruidDataSource getDruidDataSource() {
        return druidDataSource;
    }

//    public void waitThreadCount(String gaugeName, long waitThreadCount) {
//        getOrInitGauge(gaugeName).set(waitThreadCount);
//    }
//
//    public void logicConnectCount(String gaugeName, long logicConnectCount) {
//        getOrInitGauge(gaugeName).set(logicConnectCount);
//    }

    public void gaugeCollect(String gaugeName, long value) {
        getOrInitGauge(gaugeName).set(value);
    }

    public void constantsCollect(String gaugeName, String value) {
        constantsCollector.put(gaugeName, value);
    }

    private AtomicLong getOrInitGauge(String gaugeName) {
        String[] tags = {"GaugeName", gaugeName};
        final AtomicLong gauge = gaugeCollector.get(gaugeName);
        if (gauge != null) return gauge;
        synchronized (gaugeCollector) {
            if (gaugeCollector.get(gaugeName) == null) {
                final AtomicLong obj = new AtomicLong();
                Gauge.builder(gaugeName, obj, AtomicLong::get).tags(tags).register(getRegistry());
                gaugeCollector.putIfAbsent(gaugeName, obj);
            }
        }
        return gaugeCollector.get(gaugeName);
    }

}
