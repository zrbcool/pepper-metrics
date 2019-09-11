package com.pepper.metrics.integration.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.pepper.metrics.core.HealthTracker;
import com.pepper.metrics.core.MetricsRegistry;
import org.junit.Assert;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class DruidHealthTracker {

    private static Set<String> UNIQUE_NAME = new ConcurrentSkipListSet<>();

    /**
     * 添加要监控的Druid数据源
     * @param namespace         区别数据源的命名空间
     * @param druidDataSource   Druid数据源实例（需要在用户应用中创建）
     */
    public static void addDataSource(String namespace, DruidDataSource druidDataSource) {
        Assert.assertNotNull(namespace);
        Assert.assertFalse("Duplicate datasource name error.", UNIQUE_NAME.contains(namespace));
        UNIQUE_NAME.add(namespace);
        druidDataSource.setName(namespace);
        DruidHealthStats stats = new DruidHealthStats(MetricsRegistry.getREGISTRY(), namespace, druidDataSource);
        HealthTracker.addStats(stats);
    }

}
