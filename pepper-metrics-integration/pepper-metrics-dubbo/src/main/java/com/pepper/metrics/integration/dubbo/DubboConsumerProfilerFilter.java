package com.pepper.metrics.integration.dubbo;

import org.apache.dubbo.common.extension.Activate;

import java.util.concurrent.TimeUnit;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;

/**
 * Description:
 *  Dubbo服务调用方profile收集
 * @author zhiminxu
 * @version 2019-08-15
 */
@Activate(group = {CONSUMER})
public class DubboConsumerProfilerFilter extends DubboProfilerFilterTemplate {
    @Override
    void afterInvoke(String[] tags, long begin, boolean isError) {
        PROFILER_STAT_OUT.observe(System.nanoTime() - begin, TimeUnit.NANOSECONDS, tags);
        PROFILER_STAT_OUT.decConc(tags);
        if (isError) {
            PROFILER_STAT_OUT.error(tags);
        }
    }

    @Override
    void beforeInvoke(String[] tags) {
        PROFILER_STAT_OUT.incConc(tags);
    }
}
