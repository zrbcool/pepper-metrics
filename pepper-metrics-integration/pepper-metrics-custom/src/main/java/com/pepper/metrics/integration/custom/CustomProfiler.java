package com.pepper.metrics.integration.custom;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
public class CustomProfiler {
    public static final Stats CUSTOMIZED_STAT = Profiler.Builder
            .builder()
            .type("custom")
            .build();

    public static Procedure beginProcedure() {
        return new Procedure();
    }

    public static Procedure beginProcedure(String metrics, Class clz) {
        return new Procedure(metrics, clz);
    }

    public static Procedure beginProcedure(String metrics, String category) {
        return new Procedure(metrics, category);
    }

    public static class Procedure {
        private final String metrics;
        private final String category;
        private long beginTime;
        private final String[] tags;

        Procedure(String metrics, Class clz) {
            this(metrics, clz.getName());
        }

        Procedure(String metrics, String category) {
            this.category = category;
            this.metrics = category + "." + metrics;
            this.beginTime = System.currentTimeMillis();
            tags = new String[]{"operation", metrics, "class", category};
            CUSTOMIZED_STAT.incConc(tags);
        }

        Procedure() {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            category = stackTraceElement.getClassName();
            metrics = category + "." + stackTraceElement.getMethodName();
            this.beginTime = System.currentTimeMillis();
            tags = new String[]{"operation", metrics, "class", category};
            CUSTOMIZED_STAT.incConc(tags);
        }

        public String getMetrics() {
            return metrics;
        }

        public String getCategory() {
            return category;
        }

        public void exception(Throwable throwable) throws Throwable {
            CUSTOMIZED_STAT.error(tags);
            throw throwable;
        }

        public void complete() {
            CUSTOMIZED_STAT.observe(System.currentTimeMillis() - beginTime, TimeUnit.MILLISECONDS, tags);
            CUSTOMIZED_STAT.decConc(tags);
        }
    }
}
