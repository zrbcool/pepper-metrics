package com.pepper.metrics.printer.test;

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

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-07
 */
public class JedisPrinterTest {
    private Set<Stats> PROFILER_STAT_SET = Sets.newConcurrentHashSet();
    private List<ScheduledRun> extensions = null;

    @Before
    public void init() {
        try {
            extensions = ExtensionLoader.getExtensionLoader(ScheduledRun.class).getExtensions();
            final Stats jedisStat = Profiler.Builder.builder().type("jedis").namespace("default").build();
            final Stats jedisStat1 = Profiler.Builder.builder().type("jedis").namespace("jedisNamespace1").build();

            String[] jedisTags = new String[]{"method", "get"};

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

            // 初始化jedis数据
            jedisStat1.error(jedisTags);
            jedisStat1.error(jedisTags);
            jedisStat1.error(jedisTags);
            jedisStat1.observe(random.nextLong(10000), jedisTags);
            jedisStat1.observe(random.nextLong(10000), jedisTags);
            jedisStat1.observe(random.nextLong(10000), jedisTags);
            jedisStat1.observe(random.nextLong(10000), jedisTags);
            jedisStat1.observe(random.nextLong(10000), jedisTags);
            PROFILER_STAT_SET.add(jedisStat1);

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
