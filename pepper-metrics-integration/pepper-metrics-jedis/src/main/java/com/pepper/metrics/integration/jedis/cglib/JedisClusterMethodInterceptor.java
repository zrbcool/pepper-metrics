package com.pepper.metrics.integration.jedis.cglib;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
public class JedisClusterMethodInterceptor extends BaseMethodInterceptor {

    public JedisClusterMethodInterceptor(String namespace) {
        this.namespace = namespace;
    }

    @Override
    protected Object innerInvoke(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxy.invokeSuper(obj, args);
    }

    @Override
    protected String getType() {
        return "jedisCluster";
    }

}
