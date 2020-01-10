package com.pepper.metrics.core;

import com.google.common.util.concurrent.AtomicDouble;
import com.pepper.metrics.core.utils.MetricsNameBuilder;
import com.pepper.metrics.core.utils.MetricsType;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-12
 */
public abstract class HealthStatsDefault extends HealthStats {
    private final Map<String, AtomicDouble> gaugeCollector = new ConcurrentHashMap<>();

    private final Map<String, String> constantsCollector = new ConcurrentHashMap<>();

    public Map<String, AtomicDouble> getGaugeCollector() {
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

    public void infoCollect() {
        String metricsName = MetricsNameBuilder.builder()
                .setMetricsType(MetricsType.GAUGE)
                .setType(getType())
                .setSubType(getSubType())
                .setName("Info")
                .build();
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("namespace", getNamespace()));
        for (Map.Entry<String, String> entry : constantsCollector.entrySet()) {
            tags.add(Tag.of(entry.getKey(), entry.getValue()));
        }
        getRegistry().gauge(metricsName, tags, 1);
    }

    public void gaugeCollect(String gaugeName, double value) {
        getOrInitGauge(gaugeName, () -> new String[]{"GaugeName", gaugeName, "namespace", getNamespace()}).set(value);
    }

    public void gaugeCollect(String gaugeName, double value, String... additionalTags) {
        if (ArrayUtils.isEmpty(additionalTags)) {
            gaugeCollect(gaugeName, value);
        }
        String[] defaultTags = new String[]{"GaugeName", gaugeName, "namespace", getNamespace()};
        String[] tags = Arrays.copyOf(defaultTags, defaultTags.length + additionalTags.length);
        System.arraycopy(additionalTags, 0, tags, defaultTags.length, additionalTags.length);
        getOrInitGauge(gaugeName, () -> tags).set(value);

    }

    private AtomicDouble getOrInitGauge(String gaugeName, Tags tagsFuc) {
        final AtomicDouble gauge = gaugeCollector.get(gaugeName);
        if (gauge != null) return gauge;
        synchronized (gaugeCollector) {
            if (gaugeCollector.get(gaugeName) == null) {
                final AtomicDouble obj = new AtomicDouble();
                String metricsName = MetricsNameBuilder.builder()
                        .setMetricsType(MetricsType.GAUGE)
                        .setType(getType())
                        .setSubType(getSubType())
                        .setName(gaugeName)
                        .build();
                Gauge.builder(metricsName, obj, AtomicDouble::get).tags(tagsFuc.tags()).register(getRegistry());
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
