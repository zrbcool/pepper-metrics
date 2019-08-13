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
/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 * @description
 * 核心调度类，被度量目标流程中使用Profiler.Builder来构建{@link Stats}完成各项性能指标的统计，
 * 同时伴随类被加载会启动定时任务，每60秒调起所有实现了{@link ScheduledRun}的扩展点
 */
public class Profiler {
    private static final Set<Stats> PROFILER_STAT_SET = Sets.newConcurrentHashSet();
    private static final ScheduledExecutorService scheduledExecutor;

    private static MeterRegistry REGISTRY = new SimpleMeterRegistry();

    static {
        /**
         * 如果配置了多个REGISTRY工厂，仅第一个生效，防止错误产生
         */
        final List<MeterRegistryFactory> factories = ExtensionLoader.getExtensionLoader(MeterRegistryFactory.class).getExtensions();
        if (factories != null && factories.size() > 0) {
            REGISTRY = factories.get(0).createMeterRegistry();
        }
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory());
        scheduledExecutor.scheduleAtFixedRate(() -> {
            final List<ScheduledRun> extensions = ExtensionLoader.getExtensionLoader(ScheduledRun.class).getExtensions();
            for (ScheduledRun extension : extensions) {
                extension.run(PROFILER_STAT_SET);
            }
        }, 30, 60, TimeUnit.SECONDS);
    }

    public static class Builder {
        private String name;
        private String namespace = "default";

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
            final Stats stats = new Stats(REGISTRY, name, namespace);
            PROFILER_STAT_SET.add(stats);
            return stats;
        }

    }
}
