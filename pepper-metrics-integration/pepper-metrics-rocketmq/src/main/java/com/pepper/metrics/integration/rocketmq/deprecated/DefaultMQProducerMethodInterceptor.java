package com.pepper.metrics.integration.rocketmq.deprecated;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
@Deprecated
public class DefaultMQProducerMethodInterceptor implements MethodInterceptor {
    private final String namespace;
    private final Stats stats;

    public DefaultMQProducerMethodInterceptor(String namespace) {
        this.namespace = namespace;
        stats = Profiler.Builder.builder().type("rocketmq").subType("produce").namespace(namespace).build();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final String[] tags = {"method", method.getName(), "namespace", namespace};
        long begin = System.currentTimeMillis();
        Object result;
        stats.incConc(tags);
        try {
            result = proxy.invokeSuper(obj, args);
        } catch (Throwable t) {
            stats.error(tags);
            throw t;
        } finally {
            stats.observe(System.currentTimeMillis() - begin, tags);
            stats.decConc(tags);
        }
        return result;
    }
}
