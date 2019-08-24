package com.pepper.metrics.core;

import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * 统一本项目产生的线程池/线程名
 *
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
public class ThreadFactory implements java.util.concurrent.ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    ThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "pepper-pool-" +
                poolNumber.getAndIncrement() +
                "-t-";
    }

    public ThreadFactory(String prefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = prefix +
                poolNumber.getAndIncrement() +
                "-t-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
