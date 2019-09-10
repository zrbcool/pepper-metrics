package com.pepper.metrics.core;

import com.pepper.metrics.core.extension.Scope;
import com.pepper.metrics.core.extension.Spi;

import java.util.Set;

/**
 * Description:
 *
 * @author zhiminxu
 */
@Spi(scope = Scope.SINGLETON)
public interface HealthScheduledRun {
    void run(Set<HealthStats> healthStats);
}
