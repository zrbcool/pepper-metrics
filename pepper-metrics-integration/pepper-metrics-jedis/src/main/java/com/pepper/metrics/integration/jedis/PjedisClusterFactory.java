package com.pepper.metrics.integration.jedis;

import com.google.common.reflect.TypeToken;
import com.pepper.metrics.core.extension.ExtensionLoader;
import com.pepper.metrics.integration.jedis.health.JedisClusterHealthTracker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPropsHolder;

import java.util.Set;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
public class PjedisClusterFactory {

    private static String fixNamespace() {
        String namespace = "default";
        if (StringUtils.isNotEmpty(JedisPropsHolder.NAMESPACE.get())) {
            namespace = JedisPropsHolder.NAMESPACE.get();
        }
        return namespace;
    }

    private static JedisCluster newJedisCluster(Class[] classes, Object[] objects) {
        final JedisCluster jedisCluster = ExtensionLoader.getExtensionLoader(JedisClusterProxyFactory.class)
                .getExtension("cglib")
                .getProxy(JedisCluster.class, fixNamespace(), classes, objects);
        JedisClusterHealthTracker.addJedisCluster(fixNamespace(), jedisCluster);
        return jedisCluster;
    }
    private static Class[] CONST_1 = new Class[]{HostAndPort.class};
    public static JedisCluster newJedisCluster(HostAndPort node) {
        return newJedisCluster(CONST_1, new Object[]{node});
    }
    private static Class[] CONST_2 = new Class[]{HostAndPort.class, int.class};
    public static JedisCluster newJedisCluster(HostAndPort node, int timeout) {
        return newJedisCluster(CONST_2, new Object[]{node, timeout});
    }
    private static Class[] CONST_3 = new Class[]{HostAndPort.class, int.class, int.class};
    public static JedisCluster newJedisCluster(HostAndPort node, int timeout, int maxAttempts) {
        return newJedisCluster(CONST_3, new Object[]{node, timeout, maxAttempts});
    }
    private static Class[] CONST_4 = new Class[]{HostAndPort.class, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(HostAndPort node, final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_4, new Object[]{node, poolConfig});
    }
    private static Class[] CONST_5 = new Class[]{HostAndPort.class, int.class, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(HostAndPort node, int timeout, final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_5, new Object[]{node, timeout, poolConfig});
    }
    private static Class[] CONST_6 = new Class[]{HostAndPort.class, int.class, int.class, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(HostAndPort node, int timeout, int maxAttempts,
                                               final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_6, new Object[]{node, timeout, maxAttempts, poolConfig});
    }
    private static Class[] CONST_7 = new Class[]{HostAndPort.class, int.class, int.class, int.class, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(HostAndPort node, int connectionTimeout, int soTimeout,
                                               int maxAttempts, final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_7, new Object[]{node, connectionTimeout, soTimeout, maxAttempts, poolConfig});
    }
    private static Class[] CONST_8 = new Class[]{HostAndPort.class, int.class, int.class, int.class, String.class, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(HostAndPort node, int connectionTimeout, int soTimeout,
                                               int maxAttempts, String password, final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_8, new Object[]{node, connectionTimeout, soTimeout, maxAttempts, password, poolConfig});
    }
    private static Class aClass = new TypeToken<Set<HostAndPort>>() {}.getRawType();
    private static Class[] CONST_9 = new Class[]{aClass};
    public static JedisCluster newJedisCluster(Set<HostAndPort> nodes) {
        return newJedisCluster(CONST_9, new Object[]{nodes});
    }
    private static Class[] CONST_10 = new Class[]{aClass, int.class};
    public static JedisCluster newJedisCluster(Set<HostAndPort> nodes, int timeout) {
        return newJedisCluster(CONST_10, new Object[]{nodes, timeout});
    }
    private static Class[] CONST_11 = new Class[]{aClass, int.class, int.class};
    public static JedisCluster newJedisCluster(Set<HostAndPort> nodes, int timeout, int maxAttempts) {
        return newJedisCluster(CONST_11, new Object[]{nodes, timeout, maxAttempts});
    }
    private static Class[] CONST_12 = new Class[]{aClass, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(Set<HostAndPort> nodes, final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_12, new Object[]{nodes, poolConfig});
    }
    private static Class[] CONST_13 = new Class[]{aClass, int.class, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(Set<HostAndPort> nodes, int timeout, final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_13, new Object[]{nodes, timeout, poolConfig});
    }
    private static Class[] CONST_14 = new Class[]{aClass, int.class, int.class, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(Set<HostAndPort> jedisClusterNode, int timeout, int maxAttempts,
                                               final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_14, new Object[]{jedisClusterNode, timeout, maxAttempts, poolConfig});
    }
    private static Class[] CONST_15 = new Class[]{aClass, int.class, int.class, int.class, GenericObjectPoolConfig.class};
    public static JedisCluster newJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout,
                                               int maxAttempts, final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_15, new Object[]{jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, poolConfig});
    }
    private static Class[] CONST_16 = new Class[]{aClass, int.class, int.class, int.class, String.class, GenericObjectPoolConfig.class};

    public static JedisCluster newJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout,
                                               int maxAttempts, String password, final GenericObjectPoolConfig poolConfig) {
        return newJedisCluster(CONST_16, new Object[]{jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, poolConfig});
    }
    public static void main(String[] args) {
    }

}
