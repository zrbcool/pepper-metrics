package com.pepper.metrics.integration.rocketmq.deprecated;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-2
 */
@Deprecated
public class MessageListenerOrderlyProxy implements MessageListenerOrderly {

    private String namespace;
    private MessageListenerOrderly target;
    private Stats stats;
    private String consumeGroup;

    public MessageListenerOrderlyProxy(String namespace, String consumeGroup, MessageListenerOrderly target) {
        this.namespace = namespace;
        this.target = target;
        this.consumeGroup = consumeGroup;
        stats = Profiler.Builder.builder().type("rocketmq").subType("consume").namespace(namespace).build();
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        long begin = System.currentTimeMillis();
        ConsumeOrderlyStatus status;
        final MessageQueue messageQueue = context.getMessageQueue();
        final String[] tags = {"metric", messageQueue.getTopic() + "-Q" + messageQueue.getQueueId(),
                "namespace", namespace,
                "consumeGroup", consumeGroup,
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
