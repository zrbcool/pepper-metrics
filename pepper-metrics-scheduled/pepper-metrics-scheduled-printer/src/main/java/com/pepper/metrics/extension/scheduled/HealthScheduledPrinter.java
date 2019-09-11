package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.HealthScheduledRun;
import com.pepper.metrics.core.HealthStats;
import com.pepper.metrics.core.extension.ExtensionLoader;
import com.pepper.metrics.core.extension.ExtensionOrder;
import com.pepper.metrics.core.extension.SpiMeta;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

/**
 * Description:
 *
 * @author zhiminxu
 */
@SpiMeta(name = "healthScheduledPrinter")
@ExtensionOrder()
public class HealthScheduledPrinter implements HealthScheduledRun {

    @Override
    public void run(Set<HealthStats> healthStats) {
        final List<HealthPrinter> healthPrinters = ExtensionLoader.getExtensionLoader(HealthPrinter.class).getExtensions();
        String timestamp = DateTime.now().toString("yyyyMMddHHmmss");
        for (HealthPrinter healthPrinter : healthPrinters) {
            healthPrinter.print(healthStats, timestamp);
        }
    }
}
