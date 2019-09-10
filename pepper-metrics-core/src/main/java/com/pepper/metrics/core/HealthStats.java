package com.pepper.metrics.core;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class HealthStats {
    private MeterRegistry registry;
    private String name;
    private String namespace;

    public HealthStats() {
    }

    public HealthStats(MeterRegistry registry, String name, String namespace) {
        this.registry = registry;
        this.name = name;
        this.namespace = namespace;
    }

    public MeterRegistry getRegistry() {
        return registry;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }
}
