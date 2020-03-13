package com.pepper.metrics.integration.rocketmq.deprecated;

import com.pepper.metrics.core.extension.Spi;

@Deprecated
@Spi
public interface DefaultMQProducerProxyFactory {
    <T> T getProxy(Class<T> clz, String namespace, Class[] argumentTypes, Object[] arguments);

    <T> T getProxy(Class<T> clz, String namespace);
}