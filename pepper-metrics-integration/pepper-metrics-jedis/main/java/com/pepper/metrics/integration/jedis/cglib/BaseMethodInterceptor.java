package com.pepper.metrics.integration.jedis.cglib;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
public abstract class BaseMethodInterceptor implements MethodInterceptor {
    private static Map<String, Stats> statsMap = new ConcurrentHashMap<>();
    String namespace;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final String[] tags = {"method", method.getName(), "namespace", namespace};
        long begin = System.currentTimeMillis();
        Stats stats = getOrInitStats();

        stats.incConc(tags);
        Object result;
        try {
            result = method.invoke(getTarget(), args);
        } catch (Throwable t) {
            stats.error(tags);
            throw t;
        } finally {
            stats.observe(System.currentTimeMillis() - begin, tags);
            stats.decConc(tags);
        }
        return result;
    }

    protected abstract String getType();

    protected abstract Object getTarget();

    private Stats getOrInitStats() {
        String statsKey = getType() + "::" + namespace;
        Stats stats = statsMap.get(statsKey);
        if (stats != null) {
            return stats;
        }
        stats = Profiler.Builder.builder().name(getType()).namespace(namespace).build();
        statsMap.putIfAbsent(statsKey, stats);
        return statsMap.get(statsKey);
    }
}
