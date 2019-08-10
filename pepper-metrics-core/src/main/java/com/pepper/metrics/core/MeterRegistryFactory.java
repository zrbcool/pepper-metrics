package com.pepper.metrics.core;

import com.pepper.metrics.core.extension.Spi;
import io.micrometer.core.instrument.MeterRegistry;

@Spi
public interface MeterRegistryFactory {
    public MeterRegistry createMeterRegistry();
}
