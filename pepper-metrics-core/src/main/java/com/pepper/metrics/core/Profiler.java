package com.pepper.metrics.core;

import com.google.common.collect.Sets;
import com.pepper.metrics.core.extension.ExtensionLoader;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 *
 * 核心调度类，被度量目标流程中使用Profiler.Builder来构建{@link Stats}完成各项性能指标的统计，
 * 同时伴随类被加载会启动定时任务，每60秒调起所有实现了{@link ScheduledRun}的扩展点
 *
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
public class Profiler {
    protected static final Set<Stats> PROFILER_STAT_SET = Sets.newConcurrentHashSet();
    private static final ScheduledExecutorService scheduledExecutor;

    static {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory());
        scheduledExecutor.scheduleAtFixedRate(() -> {
            final List<ScheduledRun> extensions = ExtensionLoader.getExtensionLoader(ScheduledRun.class).getExtensions();
            for (ScheduledRun extension : extensions) {
                extension.run(PROFILER_STAT_SET);
            }
        }, 30, 60, TimeUnit.SECONDS);
    }

    public static class Builder {
        private String type;
        private String subType = "default";
        private String namespace = "default";

        public static Builder builder() {
            return new Builder();
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder subType(String subType) {
            this.subType = subType;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Stats build() {
            final Stats stats = new Stats(MetricsRegistry.getREGISTRY(), type, namespace, subType);
            PROFILER_STAT_SET.add(stats);
            return stats;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1, new ThreadFactory());

        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("i am alive");
            }
        }, 10000, 10000, TimeUnit.MILLISECONDS);

        for (;;) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
