package com.pepper.metrics.integration.rocketmq.health;

import com.pepper.metrics.core.HealthScheduledRun;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.ExtensionOrder;
import com.pepper.metrics.core.extension.SpiMeta;

import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */
@SpiMeta(name = "dmqPushConsumerHealthStatScheduled")
@ExtensionOrder(value = 1)
public class DMQPushConsumerHealthStatScheduled implements HealthScheduledRun {
    @Override
    public void run(Set<HealthStats> healthStats) {
        for (HealthStats healthStat : healthStats) {
            if (healthStat instanceof DMQPushConsumerHealthStats) {
                ((DMQPushConsumerHealthStats) healthStat).collectMetrics();
            }
        }
    }
}
