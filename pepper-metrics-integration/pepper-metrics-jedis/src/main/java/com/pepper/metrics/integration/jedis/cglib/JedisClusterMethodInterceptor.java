package com.pepper.metrics.integration.jedis.cglib;

import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.PjedisCluster;

import java.lang.reflect.Method;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
public class JedisClusterMethodInterceptor extends BaseMethodInterceptor {
    private PjedisCluster pjedisCluster;

    public JedisClusterMethodInterceptor(PjedisCluster jedisCluster, String namespace) {
        this.namespace = namespace;
        this.pjedisCluster = jedisCluster;
    }

    @Override
    protected Object innerInvoke(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return method.invoke(pjedisCluster, args);
    }

    @Override
    protected String getType() {
        return "jedisCluster";
    }

}
