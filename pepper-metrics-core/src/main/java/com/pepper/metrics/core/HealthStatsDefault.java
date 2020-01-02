package com.pepper.metrics.core;

import com.pepper.metrics.core.utils.MetricsNameBuilder;
import com.pepper.metrics.core.utils.MetricsType;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-12
 */
public abstract class HealthStatsDefault extends HealthStats {

    private final Map<String, AtomicLong> gaugeCollector = new ConcurrentHashMap<>();

    private final Map<String, String> constantsCollector = new ConcurrentHashMap<>();

    public Map<String, AtomicLong> getGaugeCollector() {
        return gaugeCollector;
    }

    public Map<String, String> getConstantsCollector() {
        return constantsCollector;
    }

    public HealthStatsDefault(MeterRegistry registry, String namespace) {
        super(registry, namespace);
    }

    public void constantsCollect(String gaugeName, String value) {
        constantsCollector.put(gaugeName, value);
    }

    public void gaugeCollect(String gaugeName, long value) {
        getOrInitGauge(gaugeName, () -> new String[]{"GaugeName", gaugeName, "namespace", getNamespace()}).set(value);
    }

    public void gaugeCollect(String gaugeName, long value, String... additionalTags) {
        if (ArrayUtils.isEmpty(additionalTags)) {
            gaugeCollect(gaugeName, value);

        }
        String[] defaultTags = new String[]{"GaugeName", gaugeName, "namespace", getNamespace()};
        String[] tags = Arrays.copyOf(defaultTags, defaultTags.length + additionalTags.length);
        System.arraycopy(additionalTags, 0, tags, defaultTags.length, additionalTags.length);
        getOrInitGauge(gaugeName, () -> tags).set(value);
    }

    private AtomicLong getOrInitGauge(String gaugeName, Tags tagsFuc) {
        final AtomicLong gauge = gaugeCollector.get(gaugeName);
        if (gauge != null) return gauge;
        synchronized (gaugeCollector) {
            if (gaugeCollector.get(gaugeName) == null) {
                final AtomicLong obj = new AtomicLong();
                String metricsName = MetricsNameBuilder.builder()
                        .setMetricsType(MetricsType.GAUGE)
                        .setType(getType())
                        .setSubType(getSubType())
                        .setName(gaugeName)
                        .build();
                Gauge.builder(metricsName, obj, AtomicLong::get).tags(tagsFuc.tags()).register(getRegistry());
                gaugeCollector.putIfAbsent(gaugeName, obj);
            }
        }
        return gaugeCollector.get(gaugeName);
    }

    public abstract String getType();
    public abstract String getSubType();

    public static void main(String[] args) {
        String[] defaultTags = new String[]{"GaugeName", "aa", "namespace", "ns"};
        String[] additionalTags = {"node", "node1"};
        String[] tags = Arrays.copyOf(defaultTags, defaultTags.length + additionalTags.length);
        System.arraycopy(additionalTags, 0, tags, defaultTags.length, additionalTags.length);
        System.out.println(Arrays.toString(tags));
    }
}
