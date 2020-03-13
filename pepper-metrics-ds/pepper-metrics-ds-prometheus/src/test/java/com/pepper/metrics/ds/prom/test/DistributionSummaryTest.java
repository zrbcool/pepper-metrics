package com.pepper.metrics.ds.prom.test;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-10
 */
public class DistributionSummaryTest {

    @Test
    public void test() throws InterruptedException {
        final PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        final Timer summary = Timer.builder("test")
                .distributionStatisticExpiry(Duration.ofSeconds(10))
                .publishPercentiles(0.9D, 0.99D, 0.999D)
                .distributionStatisticBufferLength(20)
                .publishPercentileHistogram(false)
                .tags(new String[]{"method", "get()"})
                .register(prometheusRegistry);
//        final DistributionSummary summary = DistributionSummary.builder("test")
//                .distributionStatisticExpiry(Duration.ofSeconds(30))
//                .publishPercentiles(0.9, 0.99, 0.999)
//                .publishPercentileHistogram(false)
//                .tags(new String[]{"method", "get()"})
//                .register(prometheusRegistry);
        AtomicInteger second = new AtomicInteger();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            final HistogramSnapshot snapshot = summary.takeSnapshot();
            final ValueAtPercentile[] valueAtPercentiles = snapshot.percentileValues();
            double p90 = 0, p99 = 0, p999 = 0;
            for (ValueAtPercentile percentile : valueAtPercentiles) {
                if (percentile.percentile() == 0.9D) {
                    p90 = percentile.value(TimeUnit.MILLISECONDS);
                } else if (percentile.percentile() == 0.99D) {
                    p99 = percentile.value(TimeUnit.MILLISECONDS);
                } else {
                    p999 = percentile.value(TimeUnit.MILLISECONDS);
                }
            }
            System.out.println(String.format("second: %s, p90: %s, p99: %s, p999: %s", second.incrementAndGet(), p90, p99, p999));
        }, 1, 1, TimeUnit.SECONDS);
        for (int j = 0; j < 100; j++) {
//            for (long i = 0; i < 1000; i++) {
//                summary.record(i, TimeUnit.MILLISECONDS);
//            }
            summary.record(j % 10, TimeUnit.MILLISECONDS);
            TimeUnit.SECONDS.sleep(1);
        }

        for (int i = 0; i < 10; i++) {
            TimeUnit.SECONDS.sleep(1);
        }

        for (int i = 0; i < 100; i++) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
