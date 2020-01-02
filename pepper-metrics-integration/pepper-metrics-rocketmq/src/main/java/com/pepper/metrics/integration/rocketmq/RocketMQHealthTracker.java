package com.pepper.metrics.integration.rocketmq;

import com.pepper.metrics.core.HealthTracker;
import com.pepper.metrics.core.MetricsRegistry;
import com.pepper.metrics.integration.rocketmq.DMQPushConsumerHealthStats;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.junit.Assert;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */
public class RocketMQHealthTracker {

    private static Set<String> UNIQUE_NAME = new ConcurrentSkipListSet<>();

    public static void addDefaultMQPushConsumer(String namespace, DefaultMQPushConsumer defaultMQPushConsumer) {
        Assert.assertNotNull(namespace);
        Assert.assertFalse("Duplicate defaultMQPushConsumer name error.", UNIQUE_NAME.contains(namespace));
        UNIQUE_NAME.add(namespace);
        DMQPushConsumerHealthStats stats = new DMQPushConsumerHealthStats(MetricsRegistry.getREGISTRY(), namespace, defaultMQPushConsumer);
        HealthTracker.addStats(stats);
    }

}
