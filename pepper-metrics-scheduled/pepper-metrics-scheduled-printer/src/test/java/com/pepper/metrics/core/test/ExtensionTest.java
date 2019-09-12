package com.pepper.metrics.core.test;

import com.google.common.collect.Sets;
import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.ScheduledRun;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.ExtensionLoader;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ExtensionTest {

    private Set<Stats> PROFILER_STAT_SET = Sets.newConcurrentHashSet();
    private List<ScheduledRun> extensions = null;

    @Before
    public void init() {
        try {
            extensions = ExtensionLoader.getExtensionLoader(ScheduledRun.class).getExtensions();
            final Stats jedisStat = Profiler.Builder.builder().type("jedis").namespace("default").build();
            final Stats httpStat = Profiler.Builder.builder().type("http").namespace("default").build();

            String[] jedisTags = new String[]{"method", "get"};
            String[] httpTags = new String[]{"url", "/ad/clickAd"};

            ThreadLocalRandom random = ThreadLocalRandom.current();

            // 初始化jedis数据
            jedisStat.error(jedisTags);
            jedisStat.error(jedisTags);
            jedisStat.error(jedisTags);
            jedisStat.observe(random.nextLong(10000), jedisTags);
            jedisStat.observe(random.nextLong(10000), jedisTags);
            jedisStat.observe(random.nextLong(10000), jedisTags);
            jedisStat.observe(random.nextLong(10000), jedisTags);
            jedisStat.observe(random.nextLong(10000), jedisTags);
            PROFILER_STAT_SET.add(jedisStat);

            // 初始化http数据
            httpStat.error(httpTags);
            httpStat.error(httpTags);
            httpStat.observe(random.nextLong(10000), httpTags);
            httpStat.observe(random.nextLong(10000), httpTags);
            PROFILER_STAT_SET.add(httpStat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exec() {
        for (ScheduledRun extension : extensions) {
            extension.run(PROFILER_STAT_SET);
        }
    }

    @Test
    public void test() {
        try {
            exec();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
