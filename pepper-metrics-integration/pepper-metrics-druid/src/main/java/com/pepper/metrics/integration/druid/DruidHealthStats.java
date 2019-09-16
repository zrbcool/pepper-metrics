package com.pepper.metrics.integration.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.pepper.metrics.core.HealthStatsDefault;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class DruidHealthStats extends HealthStatsDefault {

    private DruidDataSource druidDataSource;

    public DruidHealthStats(MeterRegistry registry, String namespace, DruidDataSource druidDataSource) {
        super(registry, namespace);
        this.druidDataSource = druidDataSource;
    }

    public DruidDataSource getDruidDataSource() {
        return druidDataSource;
    }

    @Override
    public String getType() {
        return "druid";
    }

    @Override
    public String getSubType() {
        return "default";
    }
}
