package com.pepper.metrics.sample.thread;

import com.pepper.metrics.core.HealthTracker;
import com.pepper.metrics.core.MetricsRegistry;
import com.pepper.metrics.integration.thread.ThreadHealthStats;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-9
 */
public class ThreadSample {

    public static void main(String[] args) {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        if (executorService instanceof ThreadPoolExecutor) {
            HealthTracker.addStats(new ThreadHealthStats(MetricsRegistry.getREGISTRY(), "demo", (ThreadPoolExecutor) executorService));
        }

        for (;;) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
