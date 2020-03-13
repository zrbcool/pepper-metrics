package com.pepper.metrics.sample.jediscluster;

import com.google.common.util.concurrent.RateLimiter;
import com.pepper.metrics.integration.jedis.PjedisClusterFactory;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
public class JedisClusterSampleMain {
    private static final Logger log = LoggerFactory.getLogger(JedisClusterSampleMain.class);
    private final static int defaultConnectTimeout = 2000;
    private final static int defaultConnectMaxAttempts = 20;

    public static void main(String[] args) {
        try {
            testJedisCluster();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    private static void testJedisCluster() throws InterruptedException {
        log.info("testJedisCluster()");
        String address = "redis-demo-c1-n0.coohua-inc.com:9720,redis-demo-c1-n1.coohua-inc.com:9720,redis-demo-c1-n2.coohua-inc.com:9720,redis-demo-c1-n3.coohua-inc.com:9720,redis-demo-c1-n4.coohua-inc.com:9720,redis-demo-c1-n5.coohua-inc.com:9720";

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(300);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxWaitMillis(6000);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTestOnCreate(false);

        String[] commonClusterRedisArray = address.split(",");
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        for (String clusterHostAndPort : commonClusterRedisArray) {
            String host = clusterHostAndPort.split(":")[0].trim();
            int port = Integer.parseInt(clusterHostAndPort.split(":")[1].trim());
            jedisClusterNodes.add(new HostAndPort(host, port));
        }
        JedisPropsHolder.NAMESPACE.set("cluster");
        JedisCluster jedisCluster = PjedisClusterFactory.newJedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);
        RateLimiter limiter = RateLimiter.create(1000);
        /*
         * 重要的步骤，用PjedisClusterFactory.decorateJedisCluster()包装jedisCluster即可拥有pepper-metrics-jedis的metrics能力
         * 第二个参数是namespace，当应用需要连接多组redis集群时用于区分，如果只连接一组，可以不传，默认值是default
         */
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                limiter.acquire();
                jedisCluster.setex("hello:"+ RandomUtils.nextInt(), 100, "robin");
            });
        }
        for (int i = 0; i < 100; i++) {
            for (Map.Entry<String, JedisPool> entry : jedisCluster.getClusterNodes().entrySet()) {
                log.info(String.format("%s %s NumActive:%s NumIdle:%s", i, entry.getKey(), entry.getValue().getNumActive(), entry.getValue().getNumIdle()));
            }
            log.info("------------------------------------------------------------");
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
