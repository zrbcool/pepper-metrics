package com.pepper.metrics.integration.jedis.printer;

import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.extension.scheduled.AbstractPerfPrinter;
import com.pepper.metrics.extension.scheduled.PerfPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Description:
 *  Printer的Jedis适配
 * @author zhiminxu
 * @version 2019-08-07
 */
@SpiMeta(name = "jedisPrinter")
public class JedisPrinter extends AbstractPerfPrinter implements PerfPrinter {

    @Override
    public List<Stats> chooseStats(Set<Stats> statsSet) {
        List<Stats> statsList = new ArrayList<>();
        for (Stats stats : statsSet) {
            if (stats.getType().equalsIgnoreCase("jedis")
                    || stats.getType().equalsIgnoreCase("jedisCluster")) {
                statsList.add(stats);
            }
        }
        return statsList;
    }

    @Override
    public String setPrefix(Stats stats) {
        return "perf-" + stats.getType() + "-" + stats.getNamespace();
    }
}
