package com.pepper.metrics.core;

import com.google.common.collect.Sets;
import com.pepper.metrics.core.extension.ExtensionLoader;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Profiler {
    static final Set<Stats> PROFILER_STAT_SET = Sets.newConcurrentHashSet();
    static final ScheduledExecutorService scheduledExecutor;

    public static MeterRegistry REGISTRY = new SimpleMeterRegistry();

    static {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory());
        scheduledExecutor.scheduleAtFixedRate(() -> {
            final List<ScheduledRun> extensions = ExtensionLoader.getExtensionLoader(ScheduledRun.class).getExtensions("");
            for (ScheduledRun extension : extensions) {
                extension.run(PROFILER_STAT_SET);
            }
        }, 30, 60, TimeUnit.SECONDS);
    }

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
            final Stats stats = new Stats(REGISTRY, name);
            PROFILER_STAT_SET.add(stats);
            return stats;
        }

    }
}
