package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Description:
 *
 * @author zhiminxu
 * @package com.pepper.metrics.extension.scheduled
 * @create_time 2019-08-06
 */
public class ScheduledPrinterTask implements Runnable {

    private static final Logger pLogger = LoggerFactory.getLogger("performance");

    private Set<Stats> statsSet;

    public ScheduledPrinterTask(Set<Stats> statsSet) {
        this.statsSet = statsSet;
    }

    @Override
    public void run() {

    }
}
