package com.pepper.metrics.core;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Stats {
    private MeterRegistry registry;
    private String name;

    private ConcurrentMap<List<String>, Counter> errCollector = new ConcurrentHashMap<>();
    private ConcurrentMap<List<String>, AtomicLong> gaugeCollector = new ConcurrentHashMap<>();
    private ConcurrentMap<List<String>, DistributionSummary> summaryCollector = new ConcurrentHashMap<>();

    public Stats(MeterRegistry registry, String name) {
        this.registry = registry;
        this.name = name;
    }

    public void error(String... tags) {
        getOrInitCounter(errCollector, name + ".err.counter", tags).increment();
    }

    public void incConc(String...tags) {
        getOrInitGauge(gaugeCollector, name + ".concurrent.gauge", tags).incrementAndGet();
    }

    public void decConc(String...tags) {
        getOrInitGauge(gaugeCollector, name + ".concurrent.gauge", tags).decrementAndGet();
    }

    public void observe(long elapse, String...tags) {
        getOrInitSummary(summaryCollector, name + ".summary", tags).record(elapse);
    }

    private DistributionSummary getOrInitSummary(ConcurrentMap<List<String>, DistributionSummary> collector, String sName, String... tags) {
        final List<String> asList = Arrays.asList(tags);
        DistributionSummary summary = collector.get(asList);
        if (summary != null) {
            return summary;
        }
        summary = DistributionSummary.builder(sName)
                .distributionStatisticExpiry(Duration.ofSeconds(60))
                .publishPercentiles(0.9, 0.99, 0.999)
                .tags(tags)
                .register(registry);
        collector.putIfAbsent(asList, summary);
        return summary;
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

    private AtomicLong getOrInitGauge(ConcurrentMap<List<String>, AtomicLong> collector, String gaugeName, String... tags) {
        final List<String> asList = Arrays.asList(tags);
        final AtomicLong g = collector.get(asList);
        if (g != null) return g;
        final AtomicLong obj = new AtomicLong();
        Gauge.builder(gaugeName, obj, AtomicLong::get).tags(tags).register(registry);
        collector.putIfAbsent(asList, obj);
        return obj;
    }
}
