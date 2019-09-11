package com.pepper.metrics.core;

import com.google.common.collect.Sets;
import com.pepper.metrics.core.extension.ExtensionLoader;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *  服务健康状态追踪器
 * @author zhiminxu
 */
public class HealthTracker {

    private static final Set<HealthStats> HEALTH_STAT_SET = Sets.newConcurrentHashSet();
    private static final ScheduledExecutorService scheduledExecutor;

    static {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory());
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                final List<HealthScheduledRun> extensions = ExtensionLoader.getExtensionLoader(HealthScheduledRun.class).getExtensions();
                for (HealthScheduledRun extension : extensions) {
                    extension.run(HEALTH_STAT_SET);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 30, 60, TimeUnit.SECONDS);
    }

    public static void addStats(HealthStats stats) {
        HEALTH_STAT_SET.add(stats);
    }
}
