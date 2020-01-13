package com.pepper.metrics.integration.rocketmq.health;

import com.google.common.collect.Maps;
import com.pepper.metrics.core.HealthStatsDefault;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.protocol.body.ConsumeStatus;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;

import java.util.Map;

/**
 * @author zhangrongbincool@163.com
 * @version 19-12-31
 */
public class DMQPushConsumerHealthStats extends HealthStatsDefault {

    private DefaultMQPushConsumer defaultMQPushConsumer;
    private Map<String, TopicConsumerHealthStats> topicConsumerHealthStatsMap = Maps.newConcurrentMap();

    public DMQPushConsumerHealthStats(MeterRegistry registry, String namespace, DefaultMQPushConsumer defaultMQPushConsumer) {
        super(registry, namespace);
        this.defaultMQPushConsumer = defaultMQPushConsumer;
    }

    public DefaultMQPushConsumer getDefaultMQPushConsumer() {
        return defaultMQPushConsumer;
    }

    @Override
    public String getType() {
        return "rocketmq";
    }

    @Override
    public String getSubType() {
        return "consumer";
    }

    public void collectMetrics() {
        topicConsumerHealthStatsMap.clear();
        final String consumerGroup = defaultMQPushConsumer.getConsumerGroup();
        final ConsumerRunningInfo consumerRunningInfo = defaultMQPushConsumer.getDefaultMQPushConsumerImpl().consumerRunningInfo();
        for (Map.Entry<String, ConsumeStatus> statusEntry : consumerRunningInfo.getStatusTable().entrySet()) {
            final String topic = statusEntry.getKey();
            final ConsumeStatus consumeStatus = statusEntry.getValue();
            final TopicConsumerHealthStats topicConsumerHealthStats = new TopicConsumerHealthStats(getRegistry(), getNamespace(), topic, consumerGroup, consumeStatus);
            topicConsumerHealthStatsMap.putIfAbsent(topic, topicConsumerHealthStats);
        }

        for (TopicConsumerHealthStats consumerHealthStats : topicConsumerHealthStatsMap.values()) {
            consumerHealthStats.collectMetrics();
        }
    }

    class TopicConsumerHealthStats extends HealthStatsDefault {
        private String topic;
        private ConsumeStatus consumeStatus;
        private String consumerGroup;

        public TopicConsumerHealthStats(MeterRegistry registry, String namespace, String topic, String consumerGroup, ConsumeStatus consumeStatus) {
            super(registry, namespace);
            this.topic = topic;
            this.consumeStatus = consumeStatus;
            this.consumerGroup = consumerGroup;
        }

        public void collectMetrics() {
            String[] additionTags = {"topic", topic, "consumerGroup", consumerGroup};
            constantsCollect("Topic", topic);
            gaugeCollect("ConsumeFailedMsgs", consumeStatus.getConsumeFailedMsgs(), additionTags);
            gaugeCollect("getConsumeFailedTPS", consumeStatus.getConsumeFailedTPS(), additionTags);
            gaugeCollect("ConsumeOKTPS", consumeStatus.getConsumeOKTPS(), additionTags);
            gaugeCollect("ConsumeRT", consumeStatus.getConsumeRT(), additionTags);
            gaugeCollect("PullRT", consumeStatus.getPullRT(), additionTags);
            gaugeCollect("PullTPS", consumeStatus.getPullTPS(), additionTags);
            infoCollect();
        }

        @Override
        public String getType() {
            return "rocketmq";
        }

        @Override
        public String getSubType() {
            return "consumer";
        }

    }

    public Map<String, TopicConsumerHealthStats> getTopicConsumerHealthStatsMap() {
        return topicConsumerHealthStatsMap;
    }
}
