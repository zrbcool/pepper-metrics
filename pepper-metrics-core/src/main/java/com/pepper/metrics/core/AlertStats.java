package com.pepper.metrics.core;

import com.pepper.metrics.core.utils.MetricsNameBuilder;
import com.pepper.metrics.core.utils.MetricsType;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class AlertStats extends Stats {
    public static final String ALERT_METRIC_NAME = MetricsNameBuilder.builder()
            .setName("alert")
            .setType("custom")
            .setSubType("default")
            .setMetricsType(MetricsType.GAUGE)
            .build();
    private final String[] rules;
    private final String name;

    public AlertStats(MeterRegistry registry, String type, String namespace, String subType,  List<String> rules, String name) {
        super(registry, type, namespace, subType);
        this.rules = rules.toArray(new String[0]);
        this.name = name;
    }

    private String[] assembleLabels(String label1Name, String label1Value, String label2Name, String label2Value, String label3Name, String label3Value) {
        return ArrayUtils.addAll(
                new String[]{
                        "name", name,
                        "level1_name", label1Name,
                        "level1_value", label1Value,
                        "level2_name", label2Name,
                        "level2_value", label2Value,
                        "level3_name", label3Name,
                        "level3_value", label3Value
                }, rules);
    }

    public void set(String label1Name, String label1Value, String label2Name, String label2Value, String label3Name, String label3Value, Long value) {
        String[] tagsTemp = assembleLabels(label1Name, label1Value, label2Name, label2Value, label3Name, label3Value);
        getOrInitGauge(ALERT_METRIC_NAME, tagsTemp).set(value);
    }

    public void incrementAndGet(String label1Name, String label1Value, String label2Name, String label2Value, String label3Name, String label3Value) {
        String[] tagsTemp = assembleLabels(label1Name, label1Value, label2Name, label2Value, label3Name, label3Value);
        getOrInitGauge(ALERT_METRIC_NAME, tagsTemp).incrementAndGet();
    }

    public void decrementAndGet(String label1Name, String label1Value, String label2Name, String label2Value, String label3Name, String label3Value) {
        String[] tagsTemp = assembleLabels(label1Name, label1Value, label2Name, label2Value, label3Name, label3Value);
        getOrInitGauge(ALERT_METRIC_NAME, tagsTemp).decrementAndGet();
    }

    public static void main(String[] args) {
        String name = MetricsNameBuilder.builder()
                .setName("alert")
                .setType("custom")
                .setSubType("default")
                .setMetricsType(MetricsType.GAUGE)
                .build();
        System.out.println(name);
    }
}
