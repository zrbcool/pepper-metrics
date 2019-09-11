package com.pepper.metrics.core;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class HealthStats {
    private MeterRegistry registry;
    private String namespace;

    public HealthStats() {
    }

    public HealthStats(MeterRegistry registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    public MeterRegistry getRegistry() {
        return registry;
    }

    public String getNamespace() {
        return namespace;
    }
}
