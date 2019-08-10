package com.pepper.metrics.integration.jedis.cglib;

import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.integration.jedis.ProxyFactory;
import net.sf.cglib.proxy.Enhancer;
import redis.clients.jedis.PjedisCluster;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
@SpiMeta(name = "cglib")
public class JedisCglibProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz, String namespace, Class[] argumentTypes, Object[] arguments) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new JedisMethodInterceptor(null, namespace));
        return (T) enhancer.create(argumentTypes, arguments);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz, PjedisCluster jedisCluster, String namespace) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new JedisClusterMethodInterceptor(jedisCluster, namespace));
        return (T) enhancer.create();
    }
}
