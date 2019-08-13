package com.pepper.metrics.core;

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
 * @date 19-8-7
 */
public class Stats {
    private MeterRegistry registry;
    private String name;
    private String namespace;

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

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public Stats(MeterRegistry registry, String name, String namespace) {
        this.registry = registry;
        this.name = name;
        this.namespace = namespace;
    }

    public void error(String... tags) {
        getOrInitCounter(errCollector, name + ".err.counter", tags).increment();
    }

    public void incConc(String...tags) {
        getOrInitGauge( name + ".concurrent.gauge", tags).incrementAndGet();
    }

    public void decConc(String...tags) {
        getOrInitGauge(name + ".concurrent.gauge", tags).decrementAndGet();
    }

    public void observe(long elapse, String...tags) {
        getOrInitSummary(name + ".summary", tags).record(elapse, TimeUnit.MILLISECONDS);
    }


    public void observe(long elapse, TimeUnit timeUnit, String...tags) {
        getOrInitSummary(name + ".summary", tags).record(elapse, timeUnit);
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

    private AtomicLong getOrInitGauge(String gaugeName, String... tags) {
        final List<String> asList = Arrays.asList(tags);
        final AtomicLong g = gaugeCollector.get(asList);
        if (g != null) return g;
        synchronized (gaugeCollector) {
            final AtomicLong obj = new AtomicLong();
            Gauge.builder(gaugeName, obj, AtomicLong::get).tags(tags).register(registry);
            gaugeCollector.putIfAbsent(asList, obj);
        }
        return gaugeCollector.get(asList);
    }
}
