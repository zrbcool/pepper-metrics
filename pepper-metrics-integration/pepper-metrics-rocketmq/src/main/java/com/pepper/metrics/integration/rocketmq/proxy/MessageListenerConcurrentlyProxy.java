package com.pepper.metrics.integration.rocketmq.proxy;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-2
 */
public class MessageListenerConcurrentlyProxy implements MessageListenerConcurrently {
    private Stats stats;
    private String namespace;
    private MessageListenerConcurrently target;

    public MessageListenerConcurrentlyProxy(String namespace, MessageListenerConcurrently target) {
        this.namespace = namespace;
        this.target = target;
        stats = Profiler.Builder.builder().type("rocketmq").subType("consume").namespace(namespace).build();
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        long begin = System.currentTimeMillis();
        ConsumeConcurrentlyStatus status;
        final MessageQueue messageQueue = context.getMessageQueue();
        final String[] tags = {"metric", messageQueue.getTopic() + "-Q" + messageQueue.getQueueId(),
                "namespace", namespace,
                "topic", messageQueue.getTopic(),
                "queueId", String.valueOf(messageQueue.getQueueId())};
        stats.incConc(tags);
        try {
            status = target.consumeMessage(msgs, context);
        } catch (Throwable t) {
            stats.error(tags);
            throw t;
        } finally {
            stats.observe(System.currentTimeMillis() - begin, tags);
            stats.decConc(tags);
        }
        return status;
    }
}
