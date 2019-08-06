package com.pepper.metrics.integration.jedis.jdk;

import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.integration.jedis.ProxyFactory;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Proxy;

@SpiMeta(name = "jdk")
public class JedisJdkProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clz, Jedis jedis, String namespace) {
        return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, new JedisInvocationHandler<>(clz, jedis));
    }
}
