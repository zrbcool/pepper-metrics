package com.pepper.metrics.integration.dubbo.printer;

import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.extension.scheduled.AbstractPerfPrinter;
import com.pepper.metrics.extension.scheduled.PerfPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-15
 */
@SpiMeta(name = "dubboRequestOutPrinter")
public class DubboRequestOutPrinter extends AbstractPerfPrinter implements PerfPrinter {
    @Override
    public List<Stats> chooseStats(Set<Stats> statsSet) {
        List<Stats> statsList = new ArrayList<>();
        for (Stats stats : statsSet) {
            if (stats.getType().equalsIgnoreCase("dubbo") &&
                    stats.getSubType().equalsIgnoreCase("out")) {
                statsList.add(stats);
            }
        }
        return statsList;
    }

    @Override
    public String setMetricsName(Stats stats, List<String> tags) {
        return tags.get(3) + "." + tags.get(1);
    }

    @Override
    public String setPrefix(Stats stats) {
        return "perf-dubbo:outgoing";
    }
}
