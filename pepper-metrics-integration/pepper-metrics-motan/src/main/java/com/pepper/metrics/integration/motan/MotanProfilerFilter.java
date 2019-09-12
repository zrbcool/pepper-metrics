package com.pepper.metrics.integration.motan;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangrongbin on 2018/09/27.
 */
@SpiMeta(name = "pepperProfiler")
@Activation(sequence = 2)
public class MotanProfilerFilter implements Filter {
    private static final Stats PROFILER_STAT_IN = Profiler.Builder
            .builder()
            .type("motan")
            .subType("in")
            .build();
    private static final Stats PROFILER_STAT_OUT = Profiler.Builder
            .builder()
            .type("motan")
            .subType("out")
            .build();

    @Override
    public Response filter(Caller<?> caller, Request request) {
        if (!System.getProperty("motanProfileEnable", "true").equalsIgnoreCase("true")) {
            return caller.call(request);
        }

        final String category = MotanUtils.getShortName(request.getInterfaceName());
        final String metrics = category + "." + request.getMethodName() + "(" + MotanUtils.getShortName(request.getParamtersDesc()) + ")";
        String[] tags = new String[]{"method", metrics, "service", category};

        if (StringUtils.isEmpty(metrics) || StringUtils.isEmpty(category)) {
            return caller.call(request);
        }
        long begin = System.nanoTime();
        boolean specialException = true;
        boolean isError = false;
        beforeCall(tags, caller instanceof Provider);
        try {
            final Response response = caller.call(request);
            if (response == null) {
                isError = true;
            } else {
                if (response.getException() != null) {
                    isError = true;
                }
            }
            specialException = false;
            return response;
        } finally {
            if (specialException) {
                isError = true;
            }
            postCall(tags, caller instanceof Provider, begin, isError);
        }
    }

    private void postCall(String[] tags, boolean isIncoming, long begin, boolean isError) {
        if (isIncoming) {
            PROFILER_STAT_IN.observe(System.nanoTime() - begin, TimeUnit.NANOSECONDS, tags);
            PROFILER_STAT_IN.decConc(tags);
            if (isError) {
                PROFILER_STAT_IN.error(tags);
            }
        } else {
            PROFILER_STAT_OUT.observe(System.nanoTime() - begin, TimeUnit.NANOSECONDS, tags);
            PROFILER_STAT_OUT.decConc(tags);
            if (isError) {
                PROFILER_STAT_OUT.error(tags);
            }
        }
    }

    private void beforeCall(String[] tags, boolean isIncoming) {
        if (isIncoming) {
            PROFILER_STAT_IN.incConc(tags);
        } else {
            PROFILER_STAT_OUT.incConc(tags);
        }
    }
}
