package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.Stats;
import com.pepper.metrics.extension.scheduled.domain.PrinterDomain;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 *
 * @author zhiminxu
 * @package com.pepper.metrics.extension.scheduled
 * @create_time 2019-08-07
 */
public abstract class AbstractPerfPrinter implements PerfPrinter {

    // 记录上一次的错误数，当前时间窗口的错误数 = 本次累计错误数 - 上次记录的错误数
    private volatile ConcurrentMap<String, ConcurrentMap<List<String>, Counter>> lastTimeErrCollector = new ConcurrentHashMap<>();

    private static final String SPLIT = "| ";
    private static final int LABEL_SIZE_METRICS = 75;
    private static final int LABEL_SIZE_MAX = 10;
    private static final int LABEL_SIZE_CONCURRENT = 11;
    private static final int LABEL_SIZE_ERR = 10;
    private static final int LABEL_SIZE_SUM = 10;
    private static final int LABEL_SIZE_P90 = 10;
    private static final int LABEL_SIZE_P99 = 10;
    private static final int LABEL_SIZE_P999 = 10;
    private static final int LABEL_SIZE_QPS = 8;
    private static final int LABEL_SIZE = LABEL_SIZE_METRICS +
            LABEL_SIZE_MAX +
            LABEL_SIZE_CONCURRENT +
            LABEL_SIZE_ERR +
            LABEL_SIZE_SUM +
            LABEL_SIZE_P90 +
            LABEL_SIZE_P99 +
            LABEL_SIZE_P999 +
            LABEL_SIZE_QPS +
            SPLIT.length() * 2;

    private static final Logger pLogger = LoggerFactory.getLogger("performance");

    protected static String PREFIX = "";

    @Override
    public void print(Set<Stats> statsSet) {
        List<Stats> stats = chooseStats(statsSet);
        // 记录当前时间窗口的error数
        ConcurrentMap<String, ConcurrentMap<List<String>, Counter>> currentErrCollector = new ConcurrentHashMap<>();

        for (Stats stat : stats) {
            setPre(stat);
            List<PrinterDomain> printerDomains = collector(stat, currentErrCollector);

            String prefixStr = "[" + PREFIX + "]";
            String line = StringUtils.repeat("-", LABEL_SIZE);

            pLogger.info(prefixStr + line);

            String header = prefixStr + SPLIT +
                    StringUtils.rightPad("Metrics", LABEL_SIZE_METRICS) +
                    StringUtils.leftPad("Max(ms)", LABEL_SIZE_MAX) +
                    StringUtils.leftPad("Concurrent", LABEL_SIZE_CONCURRENT) +
                    StringUtils.leftPad("Error", LABEL_SIZE_ERR) +
                    StringUtils.leftPad("Count", LABEL_SIZE_SUM) +
                    StringUtils.leftPad("P90(ms)", LABEL_SIZE_P90) +
                    StringUtils.leftPad("P99(ms)", LABEL_SIZE_P99) +
                    StringUtils.leftPad("P999(ms)", LABEL_SIZE_P999) +
                    StringUtils.leftPad("Qps", LABEL_SIZE_QPS)+
                    " " + SPLIT;
            pLogger.info(header);

            for (PrinterDomain domain : printerDomains) {
                String content = prefixStr + SPLIT +
                        StringUtils.rightPad(domain.getTag(), LABEL_SIZE_METRICS) +
                        StringUtils.leftPad(String.format("%.1f", Float.parseFloat(domain.getMax())), LABEL_SIZE_MAX) +
                        StringUtils.leftPad(String.format("%.0f", Float.parseFloat(domain.getConcurrent())), LABEL_SIZE_CONCURRENT) +
                        StringUtils.leftPad(String.format("%.0f", Float.parseFloat(domain.getErr())), LABEL_SIZE_ERR) +
                        StringUtils.leftPad(String.format("%.0f", Float.parseFloat(domain.getSum())), LABEL_SIZE_SUM) +
                        StringUtils.leftPad(String.format("%.1f", Float.parseFloat(domain.getP90())), LABEL_SIZE_P90) +
                        StringUtils.leftPad(String.format("%.1f", Float.parseFloat(domain.getP99())), LABEL_SIZE_P99) +
                        StringUtils.leftPad(String.format("%.1f", Float.parseFloat(domain.getP999())), LABEL_SIZE_P999) +
                        StringUtils.leftPad(String.format("%.1f", Float.parseFloat(domain.getQps())), LABEL_SIZE_QPS) +
                        " " + SPLIT ;
                pLogger.info(content);
            }
            pLogger.info(prefixStr + line);
        }

        this.lastTimeErrCollector = currentErrCollector;
    }

    private void setPre(Stats stats) {
        PREFIX = setPrefix(stats);
    }

    /**
     * 日志前缀的默认实现
     * @param stats
     * @return
     */
    @Override
    public String setPrefix(Stats stats) {
        return "pref-" + stats.getName() + "-" + stats.getNamespace();
    }

    private List<PrinterDomain> collector(Stats stats, ConcurrentMap<String, ConcurrentMap<List<String>, Counter>> currentErrCollector) {
        ConcurrentMap<List<String>, Counter> errCollector = stats.getErrCollector();
        ConcurrentMap<List<String>, AtomicLong> gaugeCollector = stats.getGaugeCollector();
        ConcurrentMap<List<String>, DistributionSummary> summaryCollector = stats.getSummaryCollector();

        // 记录上一次的error数
        currentErrCollector.put(buildErrCollectorKey(stats), errCollector);

        List<PrinterDomain> retList = new ArrayList<>();

        for (Map.Entry<List<String>, DistributionSummary> entry : summaryCollector.entrySet()) {
            List<String> tag = entry.getKey();
            DistributionSummary summary= entry.getValue();

            Counter counter = errCollector.get(tag);
            AtomicLong concurrent = gaugeCollector.get(tag);

            PrinterDomain domain = new PrinterDomain();
            String name = "unknown";
            if (tag.size() > 1) {
                name = tag.get(1);
            }
            HistogramSnapshot snapshot = summary.takeSnapshot();

            domain.setTag(name);
            domain.setMax(String.valueOf(snapshot.max()));
            domain.setConcurrent(concurrent == null ? "0" : concurrent.toString());
            domain.setErr(counter == null ? "0" : String.valueOf(counter.count() - getLastTimeErrCount(stats, entry.getKey())));
            domain.setSum(String.valueOf(snapshot.count()));
            ValueAtPercentile[] vps = snapshot.percentileValues();
            for (ValueAtPercentile vp : vps) {
                if (vp.percentile() == 0.9D) {
                    domain.setP90(String.valueOf(vp.value()));
                } else if (vp.percentile() == 0.99D) {
                    domain.setP99(String.valueOf(vp.value()));
                } else if (vp.percentile() == 0.999D) {
                    domain.setP999(String.valueOf(vp.value()));
                }
            }

            // 计算qps
            domain.setQps(String.format("%.1f", new Long(snapshot.count()).floatValue() / 60));

            retList.add(domain);
        }

        return retList;
    }

    private double getLastTimeErrCount(Stats stats, List<String> key) {
        ConcurrentMap<List<String>, Counter> map = this.lastTimeErrCollector.get(buildErrCollectorKey(stats));
        if (map == null) {
            return 0.0D;
        }

        Counter counter = map.get(key);
        return counter == null ? 0.0D : counter.count();
    }

    private String buildErrCollectorKey(Stats stats) {
        return stats.getName() + "-" + stats.getNamespace();
    }

}
