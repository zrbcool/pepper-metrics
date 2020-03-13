package com.pepper.metrics.sample.core;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-14
 */
public class CoreSampleMain {
    public static void main(String[] args) {
        final Stats stats = Profiler.Builder
                .builder()
                .type("custom")
                .namespace("myns")
                .build();
        String[] tags = new String[]{"method", "mockLatency()"};
        for (int i = 0; i < 10; i++) {
            stats.incConc(tags);
            long begin = System.nanoTime();
            try {
                mockLatency();
            } catch (Exception e) {
                stats.error(tags);
            } finally {
                stats.observe(System.nanoTime() - begin, TimeUnit.NANOSECONDS, tags);
                stats.decConc(tags);
            }
        }
    }

    private static void mockLatency() {
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(50, 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
