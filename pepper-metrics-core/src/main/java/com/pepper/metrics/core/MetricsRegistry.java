package com.pepper.metrics.core;

import com.pepper.metrics.core.extension.ExtensionLoader;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.List;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class MetricsRegistry {
    protected static MeterRegistry REGISTRY = new SimpleMeterRegistry();

    static {
        /**
         * 如果配置了多个REGISTRY工厂，仅第一个生效，防止错误产生
         */
        final List<MeterRegistryFactory> factories = ExtensionLoader.getExtensionLoader(MeterRegistryFactory.class).getExtensions();
        if (factories != null && factories.size() > 0) {
            REGISTRY = factories.get(0).createMeterRegistry();
        }
    }

    public static MeterRegistry getREGISTRY() {
        return REGISTRY;
    }
}
