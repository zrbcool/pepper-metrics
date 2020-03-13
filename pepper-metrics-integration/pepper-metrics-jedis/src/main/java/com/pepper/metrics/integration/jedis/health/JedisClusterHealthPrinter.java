package com.pepper.metrics.integration.jedis.health;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.extension.scheduled.AbstractHealthPrinter;

import java.util.Map;
import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-9-12
 */
@SpiMeta(name = "jedisClusterHealthPrinter")
public class JedisClusterHealthPrinter extends AbstractHealthPrinter {
    private String timestamp;

    class NodeHealthPrinter extends AbstractHealthPrinter {
        private String timestamp;
        private String node;

        public NodeHealthPrinter(String node) {
            this.node = node;
        }

        @Override
        protected String setPrefix(HealthStats healthStats) {
            return "health-jedisCluster:" + healthStats.getNamespace() + ":" + node;
        }

        @Override
        protected void doPrint(HealthStats stats) {
            if (stats instanceof JedisClusterHealthStats.JedisClusterNodeHealthStats) {
                JedisClusterHealthStats.JedisClusterNodeHealthStats nodeStats = (JedisClusterHealthStats.JedisClusterNodeHealthStats) stats;
                Map<String, AtomicDouble> gaugeCollector =  nodeStats.getGaugeCollector();
                Map<String, String> constantsCollector = nodeStats.getConstantsCollector();

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
        if (stats instanceof JedisClusterHealthStats) {
            final Map<String, JedisClusterHealthStats.JedisClusterNodeHealthStats> nodeHealthStats = ((JedisClusterHealthStats) stats).getNodeHealthStats();
            nodeHealthStats.forEach((key, value) -> {
                new NodeHealthPrinter(key).print(Sets.newHashSet(value), this.timestamp);
            });
        }
    }

    @Override
    public void print(Set<HealthStats> healthStats, String timestamp) {
        this.timestamp = timestamp;
        super.print(healthStats, timestamp);
    }
}
