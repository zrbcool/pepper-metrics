package com.pepper.metrics.core;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class Profiler {
    public static MeterRegistry REGISTRY = new SimpleMeterRegistry();

    public static class Builder {
        private String name;
        private String namespace;
        private String help;

        public static Builder builder() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Stats build() {
            return new Stats(REGISTRY, name);
        }

    }
}
