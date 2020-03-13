package com.pepper.metrics.integration.thread;

import com.pepper.metrics.core.HealthTracker;
import com.pepper.metrics.core.MetricsRegistry;

import java.util.concurrent.*;

/**
 * @author zhangrongbincool@163.com
 * @version  20-1-9
 */
public class ThreadUtil {

    public static void addThreadPool(String namespace, ExecutorService executorService) {
        HealthTracker.addStats(new ThreadHealthStats(MetricsRegistry.getREGISTRY(), namespace, (ThreadPoolExecutor) executorService));
    }

    public static void addThreadPool(String namespace, ThreadPoolExecutor threadPoolExecutor) {
        HealthTracker.addStats(new ThreadHealthStats(MetricsRegistry.getREGISTRY(), namespace, threadPoolExecutor));
    }

    public static void main(String[] args) {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        if (executorService instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
            System.out.println(String.format("ActiveCount: %s", threadPoolExecutor.getActiveCount()));
            System.out.println(String.format("CompletedTaskCount: %s", threadPoolExecutor.getCompletedTaskCount()));
            System.out.println(String.format("CorePoolSize: %s", threadPoolExecutor.getCorePoolSize()));
            System.out.println(String.format("KeepAliveTime: %s", threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS)));
            System.out.println(String.format("LargestPoolSize: %s", threadPoolExecutor.getLargestPoolSize()));
            System.out.println(String.format("MaximumPoolSize: %s", threadPoolExecutor.getMaximumPoolSize()));
            System.out.println(String.format("PoolSize: %s", threadPoolExecutor.getPoolSize()));
            System.out.println(String.format("QueueSize: %s", threadPoolExecutor.getQueue().size()));
            System.out.println(String.format("QueueRemainingCapacity: %s", threadPoolExecutor.getQueue().remainingCapacity()));
            System.out.println(String.format("RejectedExecutionHandler: %s", threadPoolExecutor.getRejectedExecutionHandler().getClass().getSimpleName()));
        } else if (executorService instanceof ForkJoinPool) {
//            monitor(registry, (ForkJoinPool) executorService);
        }
    }
}
