package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.ScheduledRun;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.ExtensionLoader;
import com.pepper.metrics.core.extension.SpiMeta;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre>
 *  Description:
 *      Performance日志打印插件
 *
 *  Notes:
 *      框架中只基于slf4j，不集成任何日志框架的实现，用户需要自行依赖日志实现
 *      需要有一个名为[performance]的appender才能打到日志中，否则会打到默认的root中
 *
 *
 * </pre>
 * @author zhiminxu
 * @version 2019-08-07
 */
@SpiMeta(name = "printer")
public class ScheduledPrinter implements ScheduledRun {

    @Override
    public void run(Set<Stats> statsSet) {
        final List<PerfPrinter> perfPrinters = ExtensionLoader.getExtensionLoader(PerfPrinter.class).getExtensions();
        String timestamp = DateTime.now().toString("yyyyMMddHHmmss");
        // 记录当前时间窗口的error数和count值
        ConcurrentMap<String, ConcurrentMap<List<String>, Double>> currentErrCollector = new ConcurrentHashMap<>();
        ConcurrentMap<String, ConcurrentMap<List<String>, Long>> currentSummaryCollector = new ConcurrentHashMap<>();

        for (PerfPrinter perfPrinter : perfPrinters) {
            perfPrinter.print(statsSet, timestamp, currentErrCollector, currentSummaryCollector);
        }

        LastTimeStatsHolder.lastTimeErrCollector = currentErrCollector;
        LastTimeStatsHolder.lastTimeSummaryCollector = currentSummaryCollector;
    }

}
