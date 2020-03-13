package com.pepper.metrics.integration.rocketmq.health;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.extension.scheduled.AbstractHealthPrinter;

import java.util.Map;
import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */
@SpiMeta(name = "dmqPushConsumerHealthPrinter")
public class DMQPushConsumerHealthPrinter extends AbstractHealthPrinter {
    private String timestamp;

    class TopicConsumerHealthPrinter extends AbstractHealthPrinter {

        @Override
        protected String setPrefix(HealthStats healthStats) {
            return "health-rocketmq:" + healthStats.getNamespace();
        }

        @Override
        protected void doPrint(HealthStats stats) {
            if (stats instanceof DMQPushConsumerHealthStats.TopicConsumerHealthStats) {
                DMQPushConsumerHealthStats.TopicConsumerHealthStats healthStats = (DMQPushConsumerHealthStats.TopicConsumerHealthStats) stats;

                final Map<String, AtomicDouble> gaugeCollector = healthStats.getGaugeCollector();
                final Map<String, String> constantsCollector = healthStats.getConstantsCollector();
                logLineMode();

                for (String key : constantsCollector.keySet()) {
                    logDataMode(buildConsLog(key, constantsCollector));
                }

                for (String key : gaugeCollector.keySet()) {
                    logDataMode(buildGaugeLog(key, gaugeCollector));
                }

                logLineMode();
            }
        }
    }

    @Override
    protected void doPrint(HealthStats stats) {
        if (stats instanceof DMQPushConsumerHealthStats) {
            final Map<String, DMQPushConsumerHealthStats.TopicConsumerHealthStats> topicConsumerHealthStatsMap = ((DMQPushConsumerHealthStats) stats).getTopicConsumerHealthStatsMap();
            topicConsumerHealthStatsMap.forEach((key, value) -> new TopicConsumerHealthPrinter().print(Sets.newHashSet(value), this.timestamp));
        }
    }

    @Override
    protected String setPrefix(HealthStats healthStats) {
        return "health-rocketmq:" + healthStats.getNamespace();
    }

    @Override
    public void print(Set<HealthStats> healthStats, String timestamp) {
        this.timestamp = timestamp;
        super.print(healthStats, timestamp);
    }
}
