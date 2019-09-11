package com.pepper.metrics.integration.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.pepper.metrics.core.HealthTracker;
import com.pepper.metrics.core.MetricsRegistry;
import com.sun.tools.javac.util.Assert;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class DruidHealthTracker {

    private static Set<String> UNIQUE_NAME = new ConcurrentSkipListSet<>();


    public static void addDataSource(String name, DruidDataSource druidDataSource) {
        addDataSource(null, name, druidDataSource);
    }

    /**
     * 添加要监控的Druid数据源
     * @param namespace         区别数据源的命名空间，默认为"default"
     * @param name              区别数据源的名称，不可为空，[namespace]:[name]必须全局唯一。
     * @param druidDataSource   Druid数据源实例（需要在用户应用中创建）
     */
    public static void addDataSource(String namespace, String name, DruidDataSource druidDataSource) {
        Assert.checkNonNull(name);
        if (StringUtils.isEmpty(namespace)) {
            namespace = "default";
        }
        String uniqueName = buildName(namespace, name);
        if (UNIQUE_NAME.contains(uniqueName)) {
            Assert.error("Duplicate datasource name error.");
        }
        UNIQUE_NAME.add(uniqueName);
        druidDataSource.setName(uniqueName);
        DruidHealthStats stats = new DruidHealthStats(MetricsRegistry.getREGISTRY(), name, namespace, druidDataSource);
        HealthTracker.addStats(stats);
    }

    private static String buildName(String namespace, String name) {
        return namespace + ":" + name;
    }

}
