package com.pepper.metrics.integration.rocketmq.perf;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import io.micrometer.core.instrument.Tags;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-2
 */
public class ProducerSendMessageHook implements SendMessageHook {
    private Stats stats;
    private String namespace;
    private ConcurrentHashMap<SendMessageContext, Long> beginTimeCache = new ConcurrentHashMap<>();

    public ProducerSendMessageHook(String namespace) {
        this.namespace = namespace;
        stats = Profiler.Builder.builder().type("rocketmq").subType("produce").namespace(namespace).build();
    }

    @Override
    public String hookName() {
        return "pepper-metrics-send-message-hook";
    }

    @Override
    public void sendMessageBefore(SendMessageContext context) {
        beginTimeCache.put(context, System.currentTimeMillis());
        final String[] tags = tags(context);
        stats.incConc(tags);
    }

    @Override
    public void sendMessageAfter(SendMessageContext context) {
        final Long beginTime = beginTimeCache.remove(context);
        final String[] tags = tags(context);
        stats.decConc(tags);
        stats.observe(System.currentTimeMillis() - beginTime, tags);
        if (context.getException() != null) {
            stats.error(tags(context, context.getException()));
        }
    }

    private String[] tags(SendMessageContext context) {
        return tags(context, null);
    }

    private String[] tags(SendMessageContext context, Exception e) {
        final MessageQueue messageQueue = context.getMq();
        if (e != null) {
            return new String[]{"metric", messageQueue.getBrokerName() +
                    "/" + messageQueue.getTopic() +
                    "/Q" + messageQueue.getQueueId(),
                    "broker", messageQueue.getBrokerName(),
                    "namespace", namespace,
                    "topic", messageQueue.getTopic(),
                    "queueId", String.valueOf(messageQueue.getQueueId()),
                    "exception", e.getClass().getName()};
        } else {
            return new String[]{"metric", messageQueue.getBrokerName() +
                    "/" + messageQueue.getTopic() +
                    "/Q" + messageQueue.getQueueId(),
                    "broker", messageQueue.getBrokerName(),
                    "namespace", namespace,
                    "topic", messageQueue.getTopic(),
                    "queueId", String.valueOf(messageQueue.getQueueId())};
        }
    }
}
