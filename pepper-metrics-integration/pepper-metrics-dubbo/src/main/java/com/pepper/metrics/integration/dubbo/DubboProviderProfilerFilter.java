package com.pepper.metrics.integration.dubbo;

import org.apache.dubbo.common.extension.Activate;

import java.util.concurrent.TimeUnit;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * Description:
 *  Dubbo服务提供方profile收集
 * @author zhiminxu
 * @version 2019-08-15
 */
@Activate(group = {PROVIDER})
public class DubboProviderProfilerFilter extends DubboProfilerFilterTemplate {
    @Override
    void afterInvoke(String[] tags, long begin, boolean isError) {
        PROFILER_STAT_IN.observe(System.nanoTime() - begin, TimeUnit.NANOSECONDS, tags);
        PROFILER_STAT_IN.decConc(tags);
        if (isError) {
            PROFILER_STAT_IN.error(tags);
        }
    }

    @Override
    void beforeInvoke(String[] tags) {
        PROFILER_STAT_IN.incConc(tags);
    }
}
