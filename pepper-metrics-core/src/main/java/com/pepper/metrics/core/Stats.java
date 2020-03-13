package com.pepper.metrics.core;

import com.pepper.metrics.core.utils.MetricsNameBuilder;
import com.pepper.metrics.core.utils.MetricsType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
/**
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
public class Stats {
    private MeterRegistry registry;
    private String type;
    private String namespace;
    private String subType;
    private String concurrentGaugeName;
    private String durationSummaryName;
    private String errCounterName;

    private final ConcurrentMap<List<String>, Counter> errCollector = new ConcurrentHashMap<>();
    private final ConcurrentMap<List<String>, AtomicLong> gaugeCollector = new ConcurrentHashMap<>();
    private final ConcurrentMap<List<String>, Timer> summaryCollector = new ConcurrentHashMap<>();


    public ConcurrentMap<List<String>, Counter> getErrCollector() {
        return errCollector;
    }

    public ConcurrentMap<List<String>, AtomicLong> getGaugeCollector() {
        return gaugeCollector;
    }

    public ConcurrentMap<List<String>, Timer> getSummaryCollector() {
        return summaryCollector;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getNamespace() {
        return namespace;
    }

    public Stats(MeterRegistry registry, String type, String namespace, String subType) {
        this.registry = registry;
        this.type = type;
        this.namespace = namespace;
        this.subType = subType;
        concurrentGaugeName = MetricsNameBuilder.builder()
                .setName("concurrent")
                .setType(type)
                .setSubType(subType)
                .setMetricsType(MetricsType.GAUGE)
                .build();

        durationSummaryName = MetricsNameBuilder.builder()
                .setName("duration")
                .setType(type)
                .setSubType(subType)
                .setMetricsType(MetricsType.SUMMARY)
                .build();

        errCounterName = MetricsNameBuilder.builder()
                .setName("err")
                .setType(type)
                .setSubType(subType)
                .setMetricsType(MetricsType.COUNTER)
                .build();
    }

    public void error(String... tags) {
        getOrInitCounter(errCollector, errCounterName, tags).increment();
    }

    public void incConc(String...tags) {
        getOrInitGauge(concurrentGaugeName, tags).incrementAndGet();
    }

    public void decConc(String...tags) {
        getOrInitGauge(concurrentGaugeName, tags).decrementAndGet();
    }

    public void observe(long elapse, String...tags) {
        getOrInitSummary(durationSummaryName, tags).record(elapse, TimeUnit.MILLISECONDS);
    }


    public void observe(long elapse, TimeUnit timeUnit, String...tags) {
        getOrInitSummary(durationSummaryName, tags).record(elapse, timeUnit);
    }

    private Timer getOrInitSummary(String sName, String... tags) {
        final List<String> asList = Arrays.asList(tags);
        Timer timer = summaryCollector.get(asList);
        if (timer != null) {
            return timer;
        }
        timer = Timer.builder(sName)
                .distributionStatisticExpiry(Duration.ofSeconds(60))
                .publishPercentiles(0.9, 0.99, 0.999, 0.99999)
                .publishPercentileHistogram(false)
                .tags(tags)
                .register(registry);
        summaryCollector.putIfAbsent(asList, timer);
        return timer;
    }

    private Counter getOrInitCounter(ConcurrentMap<List<String>, Counter> collector, String counterName, String... tags) {
        final List<String> asList = Arrays.asList(tags);
        final Counter c = collector.get(asList);
        if (c != null) {
            return c;
        }
        Counter counter = registry.counter(counterName, tags);
        collector.putIfAbsent(asList, counter);
        return counter;
    }

    protected AtomicLong getOrInitGauge(String gaugeName, String... tags) {
        final List<String> asList = Arrays.asList(tags);
        final AtomicLong g = gaugeCollector.get(asList);
        if (g != null) return g;
        synchronized (gaugeCollector) {
            if (gaugeCollector.get(asList) == null) {
                final AtomicLong obj = new AtomicLong();
                Gauge.builder(gaugeName, obj, AtomicLong::get).tags(tags).register(registry);
                gaugeCollector.putIfAbsent(asList, obj);
            }
        }
        return gaugeCollector.get(asList);
    }
}
