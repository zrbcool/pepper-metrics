package com.pepper.metrics.core;

import com.pepper.metrics.core.extension.Spi;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 * @description
 * 为了实现对接各种时序数据库如：prometheus，influx，graphite等，将创建Registry封装成扩展点
 */
@Spi
public interface MeterRegistryFactory {
    public MeterRegistry createMeterRegistry();
}
