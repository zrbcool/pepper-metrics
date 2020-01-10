package com.pepper.metrics.integration.druid;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.fastjson.JSONObject;
import com.pepper.metrics.core.HealthScheduledRun;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.ExtensionOrder;
import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.integration.druid.constants.DruidHealthQuota;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author zhiminxu
 */
@SpiMeta(name = "druidHealthStatsScheduled")
@ExtensionOrder(value = 1)
public class DruidHealthStatsScheduled implements HealthScheduledRun {

    @Override
    public void run(Set<HealthStats> healthStats) {
        Map<String, DruidHealthStats> statsMap = transferStats(healthStats);
        List<Map<String, Object>> statDataList = DruidStatManagerFacade.getInstance().getDataSourceStatDataList();

        for (Map<String, Object> map : statDataList) {
            JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(map));
            String name = data.getString("Name");
            Map<String, Object> innerMap = data.getInnerMap();

            DruidHealthStats druidHealthStats;
            if (StringUtils.isNotEmpty(name) && (druidHealthStats = statsMap.get(name)) != null) {
                druidHealthStats.constantsCollect(DruidHealthQuota.NAME, data.getOrDefault(DruidHealthQuota.NAME, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.DB_TYPE, data.getOrDefault(DruidHealthQuota.DB_TYPE, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.URL, truncateUrl(data.getOrDefault(DruidHealthQuota.URL, "null").toString()));
                druidHealthStats.constantsCollect(DruidHealthQuota.TEST_ON_BORROW, data.getOrDefault(DruidHealthQuota.TEST_ON_BORROW, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.TEST_ON_RETURN, data.getOrDefault(DruidHealthQuota.TEST_ON_RETURN, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.TEST_ON_IDLE, data.getOrDefault(DruidHealthQuota.TEST_ON_IDLE, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.DEFAULT_AUTO_COMMIT, data.getOrDefault(DruidHealthQuota.DEFAULT_AUTO_COMMIT, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.DEFAULT_READ_ONLY, data.getOrDefault(DruidHealthQuota.DEFAULT_READ_ONLY, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.DEFAULT_TRANSACTION_ISOLATION, data.getOrDefault(DruidHealthQuota.DEFAULT_TRANSACTION_ISOLATION, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.REMOVE_ABANDONED, data.getOrDefault(DruidHealthQuota.REMOVE_ABANDONED, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.KEEP_ALIVE, data.getOrDefault(DruidHealthQuota.KEEP_ALIVE, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.FAIL_FAST, data.getOrDefault(DruidHealthQuota.FAIL_FAST, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.MAX_WAIT, data.getOrDefault(DruidHealthQuota.MAX_WAIT, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.MAX_WAIT_THREAD_COUNT, data.getOrDefault(DruidHealthQuota.MAX_WAIT_THREAD_COUNT, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.POOL_PREPARED_STATEMENTS, data.getOrDefault(DruidHealthQuota.POOL_PREPARED_STATEMENTS, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.LOG_DIFFERENT_THREAD, data.getOrDefault(DruidHealthQuota.LOG_DIFFERENT_THREAD, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.USE_UNFAIR_LOCK, data.getOrDefault(DruidHealthQuota.USE_UNFAIR_LOCK, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.INIT_GLOBAL_VARIANTS, data.getOrDefault(DruidHealthQuota.INIT_GLOBAL_VARIANTS, "null").toString());
                druidHealthStats.constantsCollect(DruidHealthQuota.INIT_VARIANTS, data.getOrDefault(DruidHealthQuota.INIT_VARIANTS, "null").toString());
                if (innerMap.containsKey(DruidHealthQuota.WAIT_THREAD_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.WAIT_THREAD_COUNT, data.getLong(DruidHealthQuota.WAIT_THREAD_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.NOT_EMPTY_WAIT_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.NOT_EMPTY_WAIT_COUNT, data.getLong(DruidHealthQuota.NOT_EMPTY_WAIT_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.NOT_EMPTY_WAIT_MILLIS)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.NOT_EMPTY_WAIT_MILLIS, data.getLong(DruidHealthQuota.NOT_EMPTY_WAIT_MILLIS));
                }
                if (innerMap.containsKey(DruidHealthQuota.POOLING_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.POOLING_COUNT, data.getLong(DruidHealthQuota.POOLING_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.POOLING_PEAK)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.POOLING_PEAK, data.getLong(DruidHealthQuota.POOLING_PEAK));
                }
                if (innerMap.containsKey(DruidHealthQuota.POOLING_PEAK_TIME)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.POOLING_PEAK_TIME, data.getLong(DruidHealthQuota.POOLING_PEAK_TIME));
                }
                if (innerMap.containsKey(DruidHealthQuota.ACTIVE_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.ACTIVE_COUNT, data.getLong(DruidHealthQuota.ACTIVE_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.ACTIVE_PEAK)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.ACTIVE_PEAK, data.getLong(DruidHealthQuota.ACTIVE_PEAK));
                }
                if (innerMap.containsKey(DruidHealthQuota.ACTIVE_PEAK_TIME)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.ACTIVE_PEAK_TIME, data.getLong(DruidHealthQuota.ACTIVE_PEAK_TIME));
                }
                if (innerMap.containsKey(DruidHealthQuota.INITIAL_SIZE)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.INITIAL_SIZE, data.getLong(DruidHealthQuota.INITIAL_SIZE));
                }
                if (innerMap.containsKey(DruidHealthQuota.MIN_IDLE)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.MIN_IDLE, data.getLong(DruidHealthQuota.MIN_IDLE));
                }
                if (innerMap.containsKey(DruidHealthQuota.MAX_ACTIVE)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.MAX_ACTIVE, data.getLong(DruidHealthQuota.MAX_ACTIVE));
                }
                if (innerMap.containsKey(DruidHealthQuota.QUERY_TIMEOUT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.QUERY_TIMEOUT, data.getLong(DruidHealthQuota.QUERY_TIMEOUT));
                }
                if (innerMap.containsKey(DruidHealthQuota.TRANSACTION_QUERY_TIMEOUT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.TRANSACTION_QUERY_TIMEOUT, data.getLong(DruidHealthQuota.TRANSACTION_QUERY_TIMEOUT));
                }
                if (innerMap.containsKey(DruidHealthQuota.LOGIN_TIMEOUT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.LOGIN_TIMEOUT, data.getLong(DruidHealthQuota.LOGIN_TIMEOUT));
                }
                if (innerMap.containsKey(DruidHealthQuota.LOGIC_CONNECT_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.LOGIC_CONNECT_COUNT, data.getLong(DruidHealthQuota.LOGIC_CONNECT_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.LOGIC_CLOSE_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.LOGIC_CLOSE_COUNT, data.getLong(DruidHealthQuota.LOGIC_CLOSE_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.LOGIC_CONNECT_ERROR_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.LOGIC_CONNECT_ERROR_COUNT, data.getLong(DruidHealthQuota.LOGIC_CONNECT_ERROR_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.PHYSICAL_CONNECT_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.PHYSICAL_CONNECT_COUNT, data.getLong(DruidHealthQuota.PHYSICAL_CONNECT_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.PHYSICAL_CLOSE_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.PHYSICAL_CLOSE_COUNT, data.getLong(DruidHealthQuota.PHYSICAL_CLOSE_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.PHYSICAL_CONNECT_ERROR_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.PHYSICAL_CONNECT_ERROR_COUNT, data.getLong(DruidHealthQuota.PHYSICAL_CONNECT_ERROR_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.EXECUTE_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.EXECUTE_COUNT, data.getLong(DruidHealthQuota.EXECUTE_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.ERROR_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.ERROR_COUNT, data.getLong(DruidHealthQuota.ERROR_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.COMMIT_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.COMMIT_COUNT, data.getLong(DruidHealthQuota.COMMIT_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.ROLLBACK_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.ROLLBACK_COUNT, data.getLong(DruidHealthQuota.ROLLBACK_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.PSCACHE_ACCESS_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.PSCACHE_ACCESS_COUNT, data.getLong(DruidHealthQuota.PSCACHE_ACCESS_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.PSCACHE_HIT_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.PSCACHE_HIT_COUNT, data.getLong(DruidHealthQuota.PSCACHE_HIT_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.PSCACHE_MISS_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.PSCACHE_MISS_COUNT, data.getLong(DruidHealthQuota.PSCACHE_MISS_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.START_TRANSACTION_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.START_TRANSACTION_COUNT, data.getLong(DruidHealthQuota.START_TRANSACTION_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.CLOB_OPEN_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.CLOB_OPEN_COUNT, data.getLong(DruidHealthQuota.CLOB_OPEN_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.BLOB_OPEN_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.BLOB_OPEN_COUNT, data.getLong(DruidHealthQuota.BLOB_OPEN_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.KEEP_ALIVE_CHECK_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.KEEP_ALIVE_CHECK_COUNT, data.getLong(DruidHealthQuota.KEEP_ALIVE_CHECK_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.MAX_POOL_PREPARED_STATEMENT_PRE_CONNECTION_SIZE)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.MAX_POOL_PREPARED_STATEMENT_PRE_CONNECTION_SIZE, data.getLong(DruidHealthQuota.MAX_POOL_PREPARED_STATEMENT_PRE_CONNECTION_SIZE));
                }
                if (innerMap.containsKey(DruidHealthQuota.MIN_EVICTABLE_IDLE_TIME_MILLIS)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.MIN_EVICTABLE_IDLE_TIME_MILLIS, data.getLong(DruidHealthQuota.MIN_EVICTABLE_IDLE_TIME_MILLIS));
                }
                if (innerMap.containsKey(DruidHealthQuota.MAX_EVICTABLE_IDLE_TIME_MILLIS)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.MAX_EVICTABLE_IDLE_TIME_MILLIS, data.getLong(DruidHealthQuota.MAX_EVICTABLE_IDLE_TIME_MILLIS));
                }
                if (innerMap.containsKey(DruidHealthQuota.RECYCLE_ERROR_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.RECYCLE_ERROR_COUNT, data.getLong(DruidHealthQuota.RECYCLE_ERROR_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.PREPARED_STATEMENT_OPEN_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.PREPARED_STATEMENT_OPEN_COUNT, data.getLong(DruidHealthQuota.PREPARED_STATEMENT_OPEN_COUNT));
                }
                if (innerMap.containsKey(DruidHealthQuota.PREPARED_STATEMENT_CLOSE_COUNT)) {
                    druidHealthStats.gaugeCollect(DruidHealthQuota.PREPARED_STATEMENT_CLOSE_COUNT, data.getLong(DruidHealthQuota.PREPARED_STATEMENT_CLOSE_COUNT));
                }
                druidHealthStats.infoCollect();
            }
        }
    }

    private String truncateUrl(String aNull) {
        if (!"null".equalsIgnoreCase(aNull)) {
            int index = aNull.indexOf("?");
            if (index != -1) {
                return aNull.substring(0, index);
            }
            return aNull;
        }
        return "null";
    }

    private Map<String, DruidHealthStats> transferStats(Set<HealthStats> healthStats) {
        Map<String, DruidHealthStats> statsMap = new HashMap<>();
        for (HealthStats stats : healthStats) {
            if (stats instanceof DruidHealthStats) {
                DruidHealthStats druidHealthStats = (DruidHealthStats) stats;
                statsMap.put(druidHealthStats.getDruidDataSource().getName(), druidHealthStats);
            }
        }

        return statsMap;
    }


}
