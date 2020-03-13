package com.pepper.metrics.integration.jedis.cglib;

import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
public class JedisMethodInterceptor extends BaseMethodInterceptor {

    public JedisMethodInterceptor(String namespace) {
        this.namespace = namespace;
    }

    @Override
    protected String getType() {
        return "jedis";
    }

    @Override
    protected Object innerInvoke(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxy.invokeSuper(obj, args);
    }
}
