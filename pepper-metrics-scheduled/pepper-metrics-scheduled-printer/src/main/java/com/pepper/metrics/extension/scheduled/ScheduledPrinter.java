package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.ScheduledRun;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.SpiMeta;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

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
 * @package com.pepper.metrics.extension.scheduled
 * @create_time 2019-08-07
 */
@SpiMeta(name = "printer")
public class ScheduledPrinter implements ScheduledRun {



    @Override
    public void run(Set<Stats> statsSet) {
        IPrinter printer = new Printer();
        for (Stats stats : statsSet) {
            printer.print(stats);

//            printErr(stats.getErrCollector());
//            printGauge(stats.getGaugeCollector());
//            printSummary(stats.getSummaryCollector());
        }
    }

    private void printSummary(ConcurrentMap<List<String>, DistributionSummary> summaryCollector) {
        Set<Map.Entry<List<String>, DistributionSummary>> entries = summaryCollector.entrySet();
        for (Map.Entry<List<String>, DistributionSummary> entry : entries) {
            String key = getKey(entry.getKey());
            DistributionSummary value = entry.getValue();
            HistogramSnapshot histogramSnapshot = value.takeSnapshot();
            ValueAtPercentile[] valueAtPercentiles = histogramSnapshot.percentileValues();

            System.out.println("DistributionSummary: ==> " + key + " : [count]=" + value.count() + ", [max]=" + value.max() + ", [mean]=" + value.mean() + ", [measure]=" + value.measure() + ", [totalAmount]=" + value.totalAmount());
            System.out.println("HistogramSnapshot: ==> " + histogramSnapshot.toString());

            for (ValueAtPercentile vp : valueAtPercentiles) {
                double percentile = vp.percentile();
                double value1 = vp.value();
                System.out.println("percentage: " + percentile + " , value: " + value1);
            }
        }
    }

    private void printGauge(ConcurrentMap<List<String>, AtomicLong> gaugeCollector) {
        Set<Map.Entry<List<String>, AtomicLong>> entries = gaugeCollector.entrySet();
        for (Map.Entry<List<String>, AtomicLong> entry : entries) {
            String key = getKey(entry.getKey());
            AtomicLong value = entry.getValue();
            System.out.println(key + " : " + value.get());
        }
    }

    private void printErr(ConcurrentMap<List<String>, Counter> errCollector) {
        Set<Map.Entry<List<String>, Counter>> entries = errCollector.entrySet();
        for (Map.Entry<List<String>, Counter> entry : entries) {
            String key = getKey(entry.getKey());
            Counter counter = entry.getValue();
            System.out.println(key + " : " + counter.count());
        }
    }

    private String getKey(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append("-");
        }
        return sb.toString();
    }
}
