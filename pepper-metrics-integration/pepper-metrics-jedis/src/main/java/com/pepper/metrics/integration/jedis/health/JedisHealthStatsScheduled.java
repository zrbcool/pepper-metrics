package com.pepper.metrics.integration.jedis.health;

import com.pepper.metrics.core.HealthScheduledRun;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.ExtensionOrder;
import com.pepper.metrics.core.extension.SpiMeta;

import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version  19-9-12
 */
@SpiMeta(name = "jedisHealthStatsScheduled")
@ExtensionOrder(value = 1)
public class JedisHealthStatsScheduled implements HealthScheduledRun {
    @Override
    public void run(Set<HealthStats> healthStats) {
        for (HealthStats healthStat : healthStats) {
            if (healthStat instanceof JedisHealthStats) {
                ((JedisHealthStats) healthStat).collectStats();
            }
            if (healthStat instanceof JedisClusterHealthStats) {
                ((JedisClusterHealthStats) healthStat).collectStats();
            }
        }
    }
}
