package com.pepper.metrics.demo.jvm;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;

import java.util.concurrent.TimeUnit;

public class MAIN {
    public static void main(String[] args) throws InterruptedException {
        final Stats stats = Profiler.Builder.builder().type("http").subType("in").namespace("default").build();
        String[] tags = new String[]{"url", "/api/news1"};
        long begin = System.currentTimeMillis();
        stats.incConc(tags);
        TimeUnit.SECONDS.sleep(1);
        stats.decConc(tags);
        stats.error(tags);
        stats.observe(System.currentTimeMillis() - begin);

        System.out.println("done");


        TimeUnit.SECONDS.sleep(101);
    }
}
