package com.pepper.metrics.integration.jedis.cglib;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JedisMethodInterceptor implements MethodInterceptor {
    private static Map<String, Stats> statsMap = new ConcurrentHashMap<>();
    private Jedis jedis;
    private String namespace;

    public JedisMethodInterceptor(Jedis jedis, String namespace) {
        this.jedis = jedis;
        this.namespace = namespace;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final String[] tags = {"method", method.getName(), "namespace", namespace};
        long begin = System.currentTimeMillis();
        Stats stats = getOrInitStats();

        stats.incConc(tags);
        Object result;
        try {
            result = method.invoke(jedis, args);
        } catch (Throwable t) {
            stats.error(tags);
            throw t;
        } finally {
            stats.observe(System.currentTimeMillis() - begin, tags);
            stats.decConc(tags);
        }
        return result;
    }

    private Stats getOrInitStats() {
        Stats stats = statsMap.get(namespace);
        if (stats != null) {
            return stats;
        }
        stats = Profiler.Builder.builder().name("jedis").namespace(namespace).build();
        statsMap.putIfAbsent(namespace, stats);
        return statsMap.get(namespace);
    }
}
