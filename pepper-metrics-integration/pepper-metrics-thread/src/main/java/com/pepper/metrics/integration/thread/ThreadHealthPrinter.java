package com.pepper.metrics.integration.thread;

import com.google.common.util.concurrent.AtomicDouble;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.extension.scheduled.AbstractHealthPrinter;

import java.util.Map;

/**
 * @author zhangrongbincool@163.com
 * @version  20-1-9
 */
@SpiMeta(name = "threadHealthPrinter")
public class ThreadHealthPrinter extends AbstractHealthPrinter {

    @Override
    protected void doPrint(HealthStats stats) {
        if (stats instanceof ThreadHealthStats) {
            ThreadHealthStats healthStats = (ThreadHealthStats) stats;
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
        return "health-thread:" + healthStats.getNamespace();
    }
}
