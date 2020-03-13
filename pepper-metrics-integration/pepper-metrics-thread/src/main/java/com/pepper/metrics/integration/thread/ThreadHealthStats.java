package com.pepper.metrics.integration.thread;

import com.pepper.metrics.core.HealthStatsDefault;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version  20-1-9
 */
public class ThreadHealthStats extends HealthStatsDefault {
    private ThreadPoolExecutor threadPoolExecutor;

    public ThreadHealthStats(MeterRegistry registry, String namespace, ThreadPoolExecutor pool) {
        super(registry, namespace);
        this.threadPoolExecutor = pool;
    }

    public ThreadPoolExecutor getPool() {
        return threadPoolExecutor;
    }

    public void collectStats() {
        constantsCollect("RejectedExecutionHandler", threadPoolExecutor.getRejectedExecutionHandler().getClass().getSimpleName());
        constantsCollect("ClassName", threadPoolExecutor.getClass().getName());
        constantsCollect("KeepAliveTime", String.valueOf(threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS)));
        constantsCollect("allowsCoreThreadTimeOut", String.valueOf(threadPoolExecutor.allowsCoreThreadTimeOut()));
        constantsCollect("QueueClassName", String.valueOf(threadPoolExecutor.getQueue().getClass().getName()));
        gaugeCollect("ActiveCount", threadPoolExecutor.getActiveCount());
        gaugeCollect("CompletedTaskCount", threadPoolExecutor.getCompletedTaskCount());
        gaugeCollect("CorePoolSize", threadPoolExecutor.getCorePoolSize());
        gaugeCollect("LargestPoolSize", threadPoolExecutor.getLargestPoolSize());
        gaugeCollect("MaximumPoolSize", threadPoolExecutor.getMaximumPoolSize());
        gaugeCollect("PoolSize", threadPoolExecutor.getPoolSize());
        if (null != threadPoolExecutor.getQueue()) {
            gaugeCollect("QueueSize", threadPoolExecutor.getQueue().size());
            gaugeCollect("QueueRemainingCapacity", threadPoolExecutor.getQueue().remainingCapacity());
        }
        infoCollect();
    }

    @Override
    public String getType() {
        return "thread";
    }

    @Override
    public String getSubType() {
        return "default";
    }
}
