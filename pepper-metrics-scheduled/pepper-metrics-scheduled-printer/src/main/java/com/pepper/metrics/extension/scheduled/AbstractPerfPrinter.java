package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.Stats;
import com.pepper.metrics.extension.scheduled.domain.PrinterDomain;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-07
 */
public abstract class AbstractPerfPrinter implements PerfPrinter {

    private static final String SPLIT = "| ";
    private static final int LABEL_SIZE_METRICS = 75;
    private static final int LABEL_SIZE_MAX = 10;
    private static final int LABEL_SIZE_CONCURRENT = 11;
    private static final int LABEL_SIZE_COUNT = 15;
    private static final int LABEL_SIZE_P90 = 10;
    private static final int LABEL_SIZE_P99 = 10;
    private static final int LABEL_SIZE_P999 = 10;
    private static final int LABEL_SIZE_QPS = 8;
    private static final int LABEL_SIZE = LABEL_SIZE_METRICS +
            LABEL_SIZE_MAX +
            LABEL_SIZE_CONCURRENT +
            LABEL_SIZE_COUNT +
            LABEL_SIZE_P90 +
            LABEL_SIZE_P99 +
            LABEL_SIZE_P999 +
            LABEL_SIZE_QPS +
            SPLIT.length() * 2;

    private static final Logger pLogger = LoggerFactory.getLogger("performance");

    protected static String PREFIX = "";

    @Override
    public void print(Set<Stats> statsSet, String timestamp, ConcurrentMap<String, ConcurrentMap<List<String>, Double>> currentErrCollector, ConcurrentMap<String, ConcurrentMap<List<String>, Long>> currentSummaryCollector) {
        List<Stats> stats = chooseStats(statsSet);

        for (Stats stat : stats) {
            setPre(stat);
            List<PrinterDomain> printerDomains = collector(stat, currentErrCollector, currentSummaryCollector);

            Collections.sort(printerDomains);
            Collections.reverse(printerDomains);

            String prefixStr = "[" + PREFIX + ":" + timestamp + "] ";
            String line = StringUtils.repeat("-", LABEL_SIZE);

            pLogger.info(prefixStr + line);

            String header = prefixStr + SPLIT +
                    StringUtils.rightPad("Metrics", LABEL_SIZE_METRICS) +
                    StringUtils.leftPad("Concurrent", LABEL_SIZE_CONCURRENT) +
                    StringUtils.leftPad("Count(Err/Sum)", LABEL_SIZE_COUNT) +
                    StringUtils.leftPad("P90(ms)", LABEL_SIZE_P90) +
                    StringUtils.leftPad("P99(ms)", LABEL_SIZE_P99) +
                    StringUtils.leftPad("P999(ms)", LABEL_SIZE_P999) +
                    StringUtils.leftPad("Max(ms)", LABEL_SIZE_MAX) +
                    StringUtils.leftPad("Qps", LABEL_SIZE_QPS)+
                    " " + SPLIT;
            pLogger.info(header);

            for (PrinterDomain domain : printerDomains) {
                float err = StringUtils.isEmpty(domain.getErr()) ? 0.0F : Float.parseFloat(domain.getErr());
                float sum = StringUtils.isEmpty(domain.getSum()) ? 0.0F : Float.parseFloat(domain.getSum());
                String content = prefixStr + SPLIT +
                        StringUtils.rightPad(domain.getTag(), LABEL_SIZE_METRICS) +
                        StringUtils.leftPad(String.format("%.0f", StringUtils.isEmpty(domain.getConcurrent()) ? 0.0F : Float.parseFloat(domain.getConcurrent())), LABEL_SIZE_CONCURRENT) +
                        StringUtils.leftPad(String.format("%.0f/%.0f", err, sum), LABEL_SIZE_COUNT) +
                        StringUtils.leftPad(String.format("%.1f", StringUtils.isEmpty(domain.getP90()) ? 0.0F : Float.parseFloat(domain.getP90())), LABEL_SIZE_P90) +
                        StringUtils.leftPad(String.format("%.1f", StringUtils.isEmpty(domain.getP99()) ? 0.0F : Float.parseFloat(domain.getP99())), LABEL_SIZE_P99) +
                        StringUtils.leftPad(String.format("%.1f", StringUtils.isEmpty(domain.getP999()) ? 0.0F : Float.parseFloat(domain.getP999())), LABEL_SIZE_P999) +
                        StringUtils.leftPad(String.format("%.1f", StringUtils.isEmpty(domain.getMax()) ? 0.0F : Float.parseFloat(domain.getMax())), LABEL_SIZE_MAX) +
                        StringUtils.leftPad(String.format("%.1f", StringUtils.isEmpty(domain.getQps()) ? 0.0F : Float.parseFloat(domain.getQps())), LABEL_SIZE_QPS) +
                        " " + SPLIT ;
                pLogger.info(content);
            }
            pLogger.info(prefixStr + line);
        }
    }

    private void setPre(Stats stats) {
        PREFIX = setPrefix(stats);
    }

    @Override
    public String setMetricsName(Stats stats, List<String> tags) {
        String name = "unknown";
        if (tags.size() > 1) {
            name = tags.get(1);
        }
        return name;
    }

    /**
     * 日志前缀的默认实现
     */
    @Override
    public String setPrefix(Stats stats) {
        return "pref-" + stats.getType() + "-" + stats.getNamespace();
    }

    private List<PrinterDomain> collector(Stats stats, ConcurrentMap<String, ConcurrentMap<List<String>, Double>> currentErrCollector,
                                          ConcurrentMap<String, ConcurrentMap<List<String>, Long>> currentSummaryCollector) {
        ConcurrentMap<List<String>, Counter> errCollector = stats.getErrCollector();
        ConcurrentMap<List<String>, AtomicLong> gaugeCollector = stats.getGaugeCollector();
        ConcurrentMap<List<String>, Timer> summaryCollector = stats.getSummaryCollector();

        // 记录上一次的error数
        currentErrCollector.put(buildCollectorKey(stats), parseErrCollector(errCollector));
        currentSummaryCollector.put(buildCollectorKey(stats), parseSummaryCollector(summaryCollector));

        List<PrinterDomain> retList = new ArrayList<>();

        for (Map.Entry<List<String>, Timer> entry : summaryCollector.entrySet()) {
            List<String> tag = entry.getKey();
            Timer summary= entry.getValue();

            Counter counter = errCollector.get(tag);
            AtomicLong concurrent = gaugeCollector.get(tag);

            PrinterDomain domain = new PrinterDomain();

            String name = setMetricsName(stats, tag);
            HistogramSnapshot snapshot = summary.takeSnapshot();

            domain.setTag(name);

            domain.setConcurrent(concurrent == null ? "0" : concurrent.toString());
            domain.setErr(counter == null ? "0" : String.valueOf(counter.count() - getLastTimeErrCount(stats, entry.getKey())));
            domain.setSum(String.valueOf(snapshot.count() - getLastTimeSummaryCount(stats, entry.getKey())));
            ValueAtPercentile[] vps = snapshot.percentileValues();
            for (ValueAtPercentile vp : vps) {
                if (vp.percentile() == 0.9D) {
                    domain.setP90(String.valueOf(vp.value(TimeUnit.MILLISECONDS)));
                } else if (vp.percentile() == 0.99D) {
                    domain.setP99(String.valueOf(vp.value(TimeUnit.MILLISECONDS)));
                } else if (vp.percentile() == 0.999D) {
                    domain.setP999(String.valueOf(vp.value(TimeUnit.MILLISECONDS)));
                } else if (vp.percentile() == 0.99999D) {
                    domain.setMax(String.valueOf(vp.value(TimeUnit.MILLISECONDS)));
                }
            }

            // 计算qps
            domain.setQps(String.format("%.1f", Float.parseFloat(domain.getSum()) / 60));

            retList.add(domain);
        }

        return retList;
    }

    private ConcurrentMap<List<String>, Long> parseSummaryCollector(ConcurrentMap<List<String>, Timer> summaryCollector) {
        ConcurrentMap<List<String>, Long> map = new ConcurrentHashMap<>();

        for (Map.Entry<List<String>, Timer> entry : summaryCollector.entrySet()) {
            map.put(entry.getKey(), entry.getValue().count());
        }

        return map;
    }

    private ConcurrentMap<List<String>, Double> parseErrCollector(ConcurrentMap<List<String>, Counter> errCollector) {
        ConcurrentMap<List<String>, Double> map = new ConcurrentHashMap<>();
        for (Map.Entry<List<String>, Counter> entry : errCollector.entrySet()) {
            map.put(entry.getKey(), entry.getValue().count());
        }

        return map;
    }

    private long getLastTimeSummaryCount(Stats stats, List<String> key) {
        ConcurrentMap<List<String>, Long> map = LastTimeStatsHolder.lastTimeSummaryCollector.get(buildCollectorKey(stats));
        if (map == null) {
            return 0L;
        }

        Long summary = map.get(key);
        return summary == null ? 0L : summary;
    }

    private double getLastTimeErrCount(Stats stats, List<String> key) {
        ConcurrentMap<List<String>, Double> map = LastTimeStatsHolder.lastTimeErrCollector.get(buildCollectorKey(stats));
        if (map == null) {
            return 0.0D;
        }

        Double counter = map.get(key);
        return counter == null ? 0.0D : counter;
    }

    private String buildCollectorKey(Stats stats) {
        return stats.getType() + "-" + stats.getSubType() + "-" + stats.getNamespace();
    }

}
