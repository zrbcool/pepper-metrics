package com.pepper.metrics.integration.jedis.cglib;

import redis.clients.jedis.PjedisCluster;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
public class JedisClusterMethodInterceptor extends BaseMethodInterceptor {
    private PjedisCluster jedisCluster;

    public JedisClusterMethodInterceptor(PjedisCluster jedisCluster, String namespace) {
        this.jedisCluster = jedisCluster;
        this.namespace = namespace;
    }

    @Override
    protected String getType() {
        return "jedisCluster";
    }

    @Override
    protected Object getTarget() {
        return jedisCluster;
    }

}
