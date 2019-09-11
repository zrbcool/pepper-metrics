package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.Scope;
import com.pepper.metrics.core.extension.Spi;

import java.util.Set;

/**
 * Description:
 *
 * @author zhiminxu
 */
@Spi(scope = Scope.SINGLETON)
public interface HealthPrinter {
    void print(Set<HealthStats> healthStats, String timestamp);
}
