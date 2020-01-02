package com.pepper.metrics.integration.rocketmq.perf;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-2
 */
public class ConsumerConsumeMessageHook implements ConsumeMessageHook {
    private Stats stats;
    private String namespace;
    private ConcurrentHashMap<ConsumeMessageContext, Long> beginTimeCache = new ConcurrentHashMap<>();

    public ConsumerConsumeMessageHook(String namespace) {
        this.namespace = namespace;
        stats = Profiler.Builder.builder().type("rocketmq").subType("consume").namespace(namespace).build();
    }

    @Override
    public String hookName() {
        return "pepper-metrics-consume-message-hook";
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        beginTimeCache.put(context, System.currentTimeMillis());
        final String[] tags = tags(context);
        stats.incConc(tags);

    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        final Long beginTime = beginTimeCache.remove(context);
        final String[] tags = tags(context);
        stats.decConc(tags);
        stats.observe(System.currentTimeMillis() - beginTime, tags);
        if (!context.isSuccess()) {
            stats.error(tags);
        }
    }

    private String[] tags(ConsumeMessageContext context) {
        final MessageQueue messageQueue = context.getMq();
        return new String[]{"metric",
                    messageQueue.getBrokerName()
                            + "/" + messageQueue.getTopic()
                            + "/" + context.getConsumerGroup()
                            + "/Q" + messageQueue.getQueueId(),
                "broker", messageQueue.getBrokerName(),
                "namespace", namespace,
                "topic", messageQueue.getTopic(),
                "consumerGroup", context.getConsumerGroup(),
                "queueId", String.valueOf(messageQueue.getQueueId())};
    }
}
