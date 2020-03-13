package com.pepper.metrics.integration.jedis.health;

import com.google.common.util.concurrent.AtomicDouble;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.extension.scheduled.AbstractHealthPrinter;

import java.util.Map;

/**
 * Description:
 *
 * @author zhiminxu
 */
@SpiMeta(name = "jedisHealthPrinter")
public class JedisHealthPrinter extends AbstractHealthPrinter {

    @Override
    protected void doPrint(HealthStats stats) {
        if (stats instanceof JedisHealthStats) {
            JedisHealthStats healthStats = (JedisHealthStats) stats;
            Map<String, AtomicDouble> gaugeCollector =  healthStats.getGaugeCollector();
            Map<String, String> constantsCollector = healthStats.getConstantsCollector();

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

    @Override
    protected String setPrefix(HealthStats healthStats) {
        return "health-jedis:" + healthStats.getNamespace();
    }
}
