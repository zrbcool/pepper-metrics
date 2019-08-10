package com.pepper.metrics.sample.jediscluster;

import com.pepper.metrics.integration.jedis.PjedisClusterFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.PjedisCluster;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 */
public class JedisClusterSampleMain {
    private final static int defaultConnectTimeout = 2000;
    private final static int defaultConnectMaxAttempts = 20;

    public static void main(String[] args) {
        try {
            testJedisCluster();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testJedisCluster() throws InterruptedException {
        String address = "192.168.100.180:9700,192.168.100.180:9701,192.168.100.180:9702,192.168.100.180:9703,192.168.100.180:9704,192.168.100.180:9705";

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
        PjedisCluster jedisCluster = PjedisClusterFactory.newPjedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);

        /**
         * 重要的步骤，用PjedisClusterFactory.decorateJedisCluster()包装jedisCluster即可拥有pepper-metrics-jedis的metrics能力
         * 第二个参数是namespace，当应用需要连接多组redis集群时用于区分，如果只连接一组，可以不传，默认值是default
         */
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 10; j++) {
                jedisCluster.set("hello", "robin");
            }
            for (JedisPool jedisPool : jedisCluster.getClusterNodes().values()) {
                System.out.println(String.format("%s NumActive:%s NumIdle:%s", i, jedisPool.getNumActive(), jedisPool.getNumIdle()));
            }
            System.out.println("------------------------------------------------------------");
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
