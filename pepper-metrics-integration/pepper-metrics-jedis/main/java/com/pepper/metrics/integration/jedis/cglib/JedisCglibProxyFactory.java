package com.pepper.metrics.integration.jedis.cglib;

import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.integration.jedis.ProxyFactory;
import net.sf.cglib.proxy.Enhancer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
@SpiMeta(name = "cglib")
public class JedisCglibProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz, Jedis jedis, String namespace) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new JedisMethodInterceptor(jedis, namespace));
        return (T) enhancer.create();
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz, JedisCluster jedisCluster, String namespace) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new JedisClusterMethodInterceptor(jedisCluster, namespace));
        return (T) enhancer.create();
    }
}
