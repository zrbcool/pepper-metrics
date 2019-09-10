package com.pepper.metrics.integration.druid.printer;

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
 * @package com.pepper.metrics.integration.druid.printer
 * @create_time 2019-09-09
 */
@SpiMeta(name = "druidHealthPrinter")
public class DruidHealthPrinter extends AbstractHealthPrinter {

    @Override
    protected void doPrint(HealthStats stats) {
        if (stats instanceof DruidHealthStats) {
            DruidHealthStats druidHealthStats = (DruidHealthStats) stats;
            Map<String, AtomicLong> gaugeCollector =  druidHealthStats.getGaugeCollector();
            Map<String, String> constantsCollector = druidHealthStats.getConstantsCollector();

            logLineMode();
            logDataMode(buildConsLog(DruidHealthQuota.NAME, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.DB_TYPE, constantsCollector));

            logDataMode(buildLog(DruidHealthQuota.NOT_EMPTY_WAIT_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.NOT_EMPTY_WAIT_MILLIS, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.POOLING_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.POOLING_PEAK, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.POOLING_PEAK_TIME, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.ACTIVE_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.ACTIVE_PEAK, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.ACTIVE_PEAK_TIME, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.INITIAL_SIZE, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.MIN_IDLE, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.MAX_ACTIVE, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.QUERY_TIMEOUT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.TRANSACTION_QUERY_TIMEOUT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.LOGIN_TIMEOUT, gaugeCollector));

            logDataMode(buildConsLog(DruidHealthQuota.TEST_ON_BORROW, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.TEST_ON_RETURN, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.TEST_ON_IDLE, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.DEFAULT_AUTO_COMMIT, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.DEFAULT_READ_ONLY, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.DEFAULT_TRANSACTION_ISOLATION, constantsCollector));

            logDataMode(buildLog(DruidHealthQuota.LOGIC_CONNECT_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.LOGIC_CLOSE_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.LOGIC_CONNECT_ERROR_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.PHYSICAL_CONNECT_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.PHYSICAL_CLOSE_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.PHYSICAL_CONNECT_ERROR_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.EXECUTE_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.ERROR_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.COMMIT_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.ROLLBACK_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.PSCACHE_ACCESS_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.PSCACHE_HIT_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.PSCACHE_MISS_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.START_TRANSACTION_COUNT, gaugeCollector));

            logDataMode(buildConsLog(DruidHealthQuota.REMOVE_ABANDONED, constantsCollector));

            logDataMode(buildLog(DruidHealthQuota.CLOB_OPEN_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.BLOB_OPEN_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.KEEP_ALIVE_CHECK_COUNT, gaugeCollector));

            logDataMode(buildConsLog(DruidHealthQuota.KEEP_ALIVE, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.FAIL_FAST, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.MAX_WAIT, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.MAX_WAIT_THREAD_COUNT, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.POOL_PREPARED_STATEMENTS, constantsCollector));

            logDataMode(buildLog(DruidHealthQuota.MAX_POOL_PREPARED_STATEMENT_PRE_CONNECTION_SIZE, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.MIN_EVICTABLE_IDLE_TIME_MILLIS, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.MAX_EVICTABLE_IDLE_TIME_MILLIS, gaugeCollector));

            logDataMode(buildConsLog(DruidHealthQuota.LOG_DIFFERENT_THREAD, constantsCollector));

            logDataMode(buildLog(DruidHealthQuota.RECYCLE_ERROR_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.PREPARED_STATEMENT_OPEN_COUNT, gaugeCollector));
            logDataMode(buildLog(DruidHealthQuota.PREPARED_STATEMENT_CLOSE_COUNT, gaugeCollector));

            logDataMode(buildConsLog(DruidHealthQuota.USE_UNFAIR_LOCK, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.INIT_GLOBAL_VARIANTS, constantsCollector));
            logDataMode(buildConsLog(DruidHealthQuota.INIT_VARIANTS, constantsCollector));

            logLineMode();
        }
    }

    private String buildLog(String quota, Map<String, AtomicLong> gaugeCollector) {
        return quota + " = " + gaugeCollector.get(quota);
    }

    private String buildConsLog(String quota, Map<String, String> constantsCollector) {
        return quota + " = " + constantsCollector.get(quota);
    }
}
