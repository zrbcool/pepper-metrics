package com.pepper.metrics.integration.thread;

import com.pepper.metrics.core.HealthScheduledRun;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.ExtensionOrder;
import com.pepper.metrics.core.extension.SpiMeta;

import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version  20-1-9
 */
@SpiMeta(name = "threadHealthStatsScheduled")
@ExtensionOrder(value = 1)
public class ThreadHealthStatsScheduled implements HealthScheduledRun {
    @Override
    public void run(Set<HealthStats> healthStats) {
        for (HealthStats healthStat : healthStats) {
            if (healthStat instanceof ThreadHealthStats) {
                ((ThreadHealthStats) healthStat).collectStats();
            }
        }
    }
}
