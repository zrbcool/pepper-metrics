package com.pepper.metrics.integration.jedis;

import com.pepper.metrics.core.extension.Spi;
import redis.clients.jedis.PjedisCluster;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 * @description
 * 动态代理的生成工厂SPI扩展
 */
@Spi
public interface ProxyFactory {

    <T> T getProxy(Class<T> clz, String namespace, Class[] argumentTypes, Object[] arguments);

    @SuppressWarnings("unchecked")
    <T> T getProxy(Class<T> clz, PjedisCluster jedisCluster, String namespace);
}
