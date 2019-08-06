package com.pepper.metrics.core;

import com.pepper.metrics.core.extension.Scope;
import com.pepper.metrics.core.extension.Spi;

import java.util.Set;

@Spi(scope = Scope.SINGLETON)
public interface ScheduledRun {
    public void run(Set<Stats> statsSet);
}
