package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.ScheduledRun;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.SpiMeta;

import java.util.Set;

@SpiMeta(name = "printer")
public class ScheduledPrinter implements ScheduledRun {
    @Override
    public void run(Set<Stats> statsSet) {
        System.out.println(statsSet);
    }
}
