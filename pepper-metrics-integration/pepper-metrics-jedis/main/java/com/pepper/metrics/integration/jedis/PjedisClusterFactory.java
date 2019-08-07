package com.pepper.metrics.integration.jedis;

import com.pepper.metrics.core.extension.ExtensionLoader;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.PjedisCluster;

import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
public class PjedisClusterFactory {
    public static PjedisCluster decorateJedisCluster(PjedisCluster jedisCluster, String namespace) {
        final PjedisCluster proxy = ExtensionLoader.getExtensionLoader(ProxyFactory.class)
                .getExtension("cglib")
                .getProxy(PjedisCluster.class, jedisCluster, namespace);
        return proxy;
    }

    public static PjedisCluster decorateJedisCluster(PjedisCluster jedisCluster) {
        return decorateJedisCluster(jedisCluster, "default");
    }

    public static PjedisCluster newPjedisCluster(Set<HostAndPort> jedisClusterNodes, int defaultConnectTimeout, int defaultConnectMaxAttempts, GenericObjectPoolConfig jedisPoolConfig, String namespace) {
        PjedisCluster pjedisCluster = new PjedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);
        pjedisCluster = decorateJedisCluster(pjedisCluster, namespace);
        return pjedisCluster;
    }

    public static PjedisCluster newPjedisCluster(Set<HostAndPort> jedisClusterNodes, int defaultConnectTimeout, int defaultConnectMaxAttempts, GenericObjectPoolConfig jedisPoolConfig) {
        return newPjedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);
    }


}
