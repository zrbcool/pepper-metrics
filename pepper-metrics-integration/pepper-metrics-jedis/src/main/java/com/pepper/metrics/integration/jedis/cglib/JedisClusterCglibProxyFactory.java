package com.pepper.metrics.integration.jedis.cglib;

import com.pepper.metrics.core.extension.SpiMeta;
import com.pepper.metrics.integration.jedis.JedisClusterProxyFactory;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-20
 */
@SpiMeta(name = "cglib")
public class JedisClusterCglibProxyFactory implements JedisClusterProxyFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz, String namespace, Class[] argumentTypes, Object[] arguments) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new JedisClusterMethodInterceptor(namespace));
        return (T) enhancer.create(argumentTypes, arguments);
    }
}
