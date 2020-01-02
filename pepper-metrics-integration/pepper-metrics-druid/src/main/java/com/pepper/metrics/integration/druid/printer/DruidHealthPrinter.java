package com.pepper.metrics.integration.druid.printer;

import com.google.common.util.concurrent.AtomicDouble;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.extension.scheduled.AbstractHealthPrinter;
import com.pepper.metrics.integration.druid.DruidHealthStats;
import com.pepper.metrics.integration.druid.constants.DruidHealthQuota;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 *
 * @author zhiminxu
 */
@SpiMeta(name = "druidHealthPrinter")
public class DruidHealthPrinter extends AbstractHealthPrinter {

    @Override
    protected void doPrint(HealthStats stats) {
        if (stats instanceof DruidHealthStats) {
            DruidHealthStats druidHealthStats = (DruidHealthStats) stats;
            Map<String, AtomicDouble> gaugeCollector =  druidHealthStats.getGaugeCollector();
            Map<String, String> constantsCollector = druidHealthStats.getConstantsCollector();

            logLineMode();
            logDataMode(buildConsLog(DruidHealthQuota.NAME, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.DB_TYPE, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.URL, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.TEST_ON_BORROW, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.TEST_ON_RETURN, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.TEST_ON_IDLE, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.DEFAULT_AUTO_COMMIT, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.DEFAULT_READ_ONLY, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.DEFAULT_TRANSACTION_ISOLATION, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.REMOVE_ABANDONED, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.KEEP_ALIVE, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.FAIL_FAST, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.MAX_WAIT, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.MAX_WAIT_THREAD_COUNT, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.POOL_PREPARED_STATEMENTS, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.LOG_DIFFERENT_THREAD, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.USE_UNFAIR_LOCK, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.INIT_GLOBAL_VARIANTS, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.INIT_VARIANTS, constantsCollector));

            if (gaugeCollector.containsKey(DruidHealthQuota.WAIT_THREAD_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.WAIT_THREAD_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.NOT_EMPTY_WAIT_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.NOT_EMPTY_WAIT_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.NOT_EMPTY_WAIT_MILLIS)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.NOT_EMPTY_WAIT_MILLIS, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.POOLING_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.POOLING_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.POOLING_PEAK)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.POOLING_PEAK, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.POOLING_PEAK_TIME)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.POOLING_PEAK_TIME, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.ACTIVE_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.ACTIVE_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.ACTIVE_PEAK)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.ACTIVE_PEAK, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.ACTIVE_PEAK_TIME)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.ACTIVE_PEAK_TIME, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.INITIAL_SIZE)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.INITIAL_SIZE, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.MIN_IDLE)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.MIN_IDLE, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.MAX_ACTIVE)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.MAX_ACTIVE, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.QUERY_TIMEOUT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.QUERY_TIMEOUT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.TRANSACTION_QUERY_TIMEOUT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.TRANSACTION_QUERY_TIMEOUT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.LOGIN_TIMEOUT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.LOGIN_TIMEOUT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.LOGIC_CONNECT_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.LOGIC_CONNECT_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.LOGIC_CLOSE_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.LOGIC_CLOSE_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.LOGIC_CONNECT_ERROR_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.LOGIC_CONNECT_ERROR_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.PHYSICAL_CONNECT_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.PHYSICAL_CONNECT_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.PHYSICAL_CLOSE_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.PHYSICAL_CLOSE_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.PHYSICAL_CONNECT_ERROR_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.PHYSICAL_CONNECT_ERROR_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.EXECUTE_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.EXECUTE_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.ERROR_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.ERROR_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.COMMIT_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.COMMIT_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.ROLLBACK_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.ROLLBACK_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.PSCACHE_ACCESS_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.PSCACHE_ACCESS_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.PSCACHE_HIT_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.PSCACHE_HIT_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.PSCACHE_MISS_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.PSCACHE_MISS_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.START_TRANSACTION_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.START_TRANSACTION_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.CLOB_OPEN_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.CLOB_OPEN_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.BLOB_OPEN_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.BLOB_OPEN_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.KEEP_ALIVE_CHECK_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.KEEP_ALIVE_CHECK_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.MAX_POOL_PREPARED_STATEMENT_PRE_CONNECTION_SIZE)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.MAX_POOL_PREPARED_STATEMENT_PRE_CONNECTION_SIZE, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.MIN_EVICTABLE_IDLE_TIME_MILLIS)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.MIN_EVICTABLE_IDLE_TIME_MILLIS, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.MAX_EVICTABLE_IDLE_TIME_MILLIS)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.MAX_EVICTABLE_IDLE_TIME_MILLIS, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.RECYCLE_ERROR_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.RECYCLE_ERROR_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.PREPARED_STATEMENT_OPEN_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.PREPARED_STATEMENT_OPEN_COUNT, gaugeCollector));
            }
            if (gaugeCollector.containsKey(DruidHealthQuota.PREPARED_STATEMENT_CLOSE_COUNT)) {
                logDataMode(buildGaugeLog(DruidHealthQuota.PREPARED_STATEMENT_CLOSE_COUNT, gaugeCollector));
            }

            logLineMode();
        }
    }

    @Override
    protected String setPrefix(HealthStats healthStats) {
        return "health-druid:" + healthStats.getNamespace();
    }
}
