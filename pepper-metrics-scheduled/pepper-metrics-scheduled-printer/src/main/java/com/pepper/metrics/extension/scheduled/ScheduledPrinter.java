package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.ScheduledRun;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.SpiMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@SpiMeta(name = "printer")
public class ScheduledPrinter implements ScheduledRun {

    private static final Logger pLogger = LoggerFactory.getLogger("performance");

    @Override
    public void run(Set<Stats> statsSet) {
        for (Stats stats : statsSet) {
        }


        System.out.println(statsSet);
    }
}
