package com.pepper.metrics.integration.dubbo;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.*;

/**
 * <pre>
 * Description:
 *  Dubbo profile收集模板类，子类需要实现以下两个模板方法：
 *  1）abstract void afterInvoke(String[] tags, long begin, boolean isError);
 *  2）abstract void beforeInvoke(String[] tags);
 * </pre>
 * @author zhiminxu
 * @version 2019-08-15
 */
public abstract class DubboProfilerFilterTemplate implements Filter {

    static final Stats PROFILER_STAT_IN = Profiler.Builder
            .builder()
            .type("dubbo")
            .subType("in")
            .build();
    static final Stats PROFILER_STAT_OUT = Profiler.Builder
            .builder()
            .type("dubbo")
            .subType("out")
            .build();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        final String category = invoker.getInterface().getSimpleName(); // 接口名
        StringBuilder params = new StringBuilder();
        Class[] clazzs = invocation.getParameterTypes();
        for (int index = 0; index < clazzs.length; index++) {
            params.append(index == clazzs.length - 1 ? clazzs[index].getSimpleName() : clazzs[index].getSimpleName() + ", ");
        }

        final String metrics = invocation.getMethodName() + "(" + params.toString() + ")"; // method(参数类型...)

        String[] tags = new String[]{"method", metrics, "service", category};

        if (StringUtils.isEmpty(category) || StringUtils.isEmpty(metrics)) {
            return invoker.invoke(invocation);
        }

        long begin = System.nanoTime();
        boolean specialException = true;
        boolean isError = false;

        // before trace...
        beforeInvoke(tags);
        try {
            Result result = invoker.invoke(invocation);
            if (result == null || result.hasException()) {
                isError = true;
            }

            specialException = false;

            return result;
        } finally {
            if (specialException) {
                isError = true;
            }
            // after trace...
            afterInvoke(tags, begin, isError);
        }
    }

    abstract void afterInvoke(String[] tags, long begin, boolean isError);

    abstract void beforeInvoke(String[] tags);
}
