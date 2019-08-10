package com.pepper.metrics.core.test;

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
 * @package com.pepper.metrics.integration.jedis.printer
 * @create_time 2019-08-07
 */
@SpiMeta(name = "jedisPrinter")
public class JedisPrinter extends AbstractPerfPrinter implements PerfPrinter {

    @Override
    public List<Stats> chooseStats(Set<Stats> statsSet) {
        List<Stats> statsList = new ArrayList<>();
        for (Stats stats : statsSet) {
            if (stats.getName().equalsIgnoreCase("jedis")
                    || stats.getName().equalsIgnoreCase("jedisCluster")) {
                statsList.add(stats);
            }
        }
        return statsList;
    }

    @Override
    public String setPrefix(Stats stats) {
        return "perf-" + stats.getName() + "-" + stats.getNamespace();
    }
}
