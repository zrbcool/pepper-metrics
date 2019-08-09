package com.pepper.metrics.core.test;

import com.google.common.collect.Sets;
import com.pepper.metrics.core.ScheduledRun;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.ExtensionLoader;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class ExtensionTest {
    @Test
    public void test() {
        Set<Stats> PROFILER_STAT_SET = Sets.newConcurrentHashSet();
        final List<ScheduledRun> extensions = ExtensionLoader.getExtensionLoader(ScheduledRun.class).getExtensions();
        for (ScheduledRun extension : extensions) {
            extension.run(PROFILER_STAT_SET);
        }
    }
}
