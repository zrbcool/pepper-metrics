package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.Stats;
import com.pepper.metrics.extension.scheduled.domain.PrinterDomain;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description:
 *  Printer的默认实现
 *
 * @author zhiminxu
 * @package com.pepper.metrics.extension.scheduled
 * @create_time 2019-08-07
 */
public class Printer extends AbstractPrinter {

    @Override
    public void setPrefix(Stats stats) {
        PREFIX = "perf-" + stats.getName();
    }

    @Override
    public List<PrinterDomain> collector(Stats stats) {
        ConcurrentMap<List<String>, Counter> errCollector = stats.getErrCollector();
        ConcurrentMap<List<String>, AtomicLong> gaugeCollector = stats.getGaugeCollector();
        ConcurrentMap<List<String>, DistributionSummary> summaryCollector = stats.getSummaryCollector();

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
            domain.setErr(counter == null ? "0" : String.valueOf(counter.count()));
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

}
