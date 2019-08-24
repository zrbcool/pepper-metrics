package com.pepper.metrics.integration.jedis;

import com.pepper.metrics.core.extension.Spi;

/**
 *
 * 动态代理的生成工厂SPI扩展
 *
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
@Spi
public interface JedisClusterProxyFactory {

    <T> T getProxy(Class<T> clz, String namespace, Class[] argumentTypes, Object[] arguments);
}
