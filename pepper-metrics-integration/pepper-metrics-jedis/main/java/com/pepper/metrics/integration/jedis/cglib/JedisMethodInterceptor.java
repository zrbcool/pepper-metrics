package com.pepper.metrics.integration.jedis.cglib;

import redis.clients.jedis.Jedis;
/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
public class JedisMethodInterceptor extends BaseMethodInterceptor {
    protected Jedis jedis;

    public JedisMethodInterceptor(Jedis jedis, String namespace) {
        this.jedis = jedis;
        this.namespace = namespace;
    }

    @Override
    protected String getType() {
        return "jedis";
    }

    @Override
    protected Object getTarget() {
        return jedis;
    }
}
