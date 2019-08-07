package com.pepper.metrics.integration.jedis;

import com.pepper.metrics.core.extension.ExtensionLoader;
import redis.clients.jedis.JedisCluster;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
public class PjedisClusterFactory {
    public static JedisCluster decorateJedisCluster(JedisCluster jedisCluster, String namespace) {
        final JedisCluster proxy = ExtensionLoader.getExtensionLoader(ProxyFactory.class)
                .getExtension("cglib")
                .getProxy(JedisCluster.class, jedisCluster, namespace);
        return proxy;
    }

    public static JedisCluster decorateJedisCluster(JedisCluster jedisCluster) {
        return decorateJedisCluster(jedisCluster, "default");
    }
}
