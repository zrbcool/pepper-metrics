package com.pepper.metrics.extension.scheduled;

import com.google.common.util.concurrent.AtomicDouble;
import com.pepper.metrics.core.HealthStats;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author zhiminxu
 */
public abstract class AbstractHealthPrinter implements HealthPrinter {

    protected static final Logger pLogger = LoggerFactory.getLogger("performance");

    protected String prefix = "";

    protected static final String LEFT_STR = "| ";
    protected static final String RIGHT_STR = "|";

    protected static final int LINE_LENGTH = 100;

    protected static ThreadLocal<String> LINE_MODE = new ThreadLocal<>();
    protected static ThreadLocal<String> DATA_MODE = new ThreadLocal<>();

    @Override
    public void print(Set<HealthStats> healthStats, String timestamp) {
        for (HealthStats stats : healthStats) {
            setPre(stats);
            String prefixStr = "[" + prefix + ":" + timestamp + "] ";
            LINE_MODE.set(prefixStr + StringUtils.repeat("-", LINE_LENGTH));
            DATA_MODE.set(prefixStr + LEFT_STR);
            doPrint(stats);
        }
    }

    protected abstract void doPrint(HealthStats stats);

    protected void logLineMode() {
        pLogger.info(LINE_MODE.get());
    }

    protected void logDataMode(String data) {
        int originLength = LEFT_STR.length() + data.length();
        String blankStr = StringUtils.repeat(" ", LINE_LENGTH - originLength - RIGHT_STR.length());
        pLogger.info(DATA_MODE.get() + data + blankStr + RIGHT_STR);
    }

    private void setPre(HealthStats healthStats) {
        prefix = setPrefix(healthStats);
    }

    protected String setPrefix(HealthStats healthStats) {
        return "health-" + healthStats.getNamespace();
    }

    protected String buildGaugeLog(String key, Map<String, AtomicDouble> gaugeCollector) {
        return key + " = " + gaugeCollector.get(key);
    }

    protected String buildConsLog(String key, Map<String, String> constantsCollector) {
        return key + " = " + constantsCollector.get(key);
    }
}
