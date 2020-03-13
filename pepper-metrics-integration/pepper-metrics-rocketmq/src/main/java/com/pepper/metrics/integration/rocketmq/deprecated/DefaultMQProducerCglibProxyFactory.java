package com.pepper.metrics.integration.rocketmq.deprecated;

import com.pepper.metrics.core.extension.SpiMeta;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-2
 */
@Deprecated
@SpiMeta(name = "cglib")
public class DefaultMQProducerCglibProxyFactory implements DefaultMQProducerProxyFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz, String namespace, Class[] argumentTypes, Object[] arguments) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new DefaultMQProducerMethodInterceptor(namespace));
        return (T) enhancer.create(argumentTypes, arguments);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz, String namespace) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new DefaultMQProducerMethodInterceptor(namespace));
        return (T) enhancer.create();
    }
}
