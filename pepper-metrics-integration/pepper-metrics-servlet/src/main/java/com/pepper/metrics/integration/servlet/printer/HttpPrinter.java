package com.pepper.metrics.integration.servlet.printer;

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
 * @package com.pepper.metrics.integration.servlet.printer
 * @create_time 2019-08-13
 */
@SpiMeta(name = "httpPrinter")
public class HttpPrinter extends AbstractPerfPrinter implements PerfPrinter {
    @Override
    public List<Stats> chooseStats(Set<Stats> statsSet) {
        List<Stats> statsList = new ArrayList<>();
        for (Stats stats : statsSet) {
            if (stats.getName().equalsIgnoreCase("http")) {
                statsList.add(stats);
            }
        }
        return statsList;
    }

    @Override
    public String setMetricsName(Stats stats, List<String> tags) {
        // 格式：[Http Method] [url]
        return tags.get(1) + " " + tags.get(3);
    }

    @Override
    protected boolean printFilter(Stats stats, List<String> tag) {
        // 如果 > 6，说明记录的是httpStatus指标，不打印到日志中
        return tag.size() <= 6;
    }
}
