package com.pepper.metrics.integration.jedis.jdk;

import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JedisInvocationHandler<T> implements InvocationHandler {
    private Class<T> clz;
    private Jedis jedis;
    public JedisInvocationHandler(Class<T> clz, Jedis jedis) {
        this.clz = clz;
        this.jedis = jedis;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before " + method.getName());
        final Object result = method.invoke(proxy, args);
        System.out.println("after " + method.getName());
        return result;
    }
}
