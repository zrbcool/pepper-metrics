package com.pepper.metrics.integration.rocketmq.perf;

import com.pepper.metrics.core.MetricsRegistry;
import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.utils.MetricsNameBuilder;
import com.pepper.metrics.core.utils.MetricsType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeReturnType;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-2
 */
public class ConsumerConsumeMessageHook implements ConsumeMessageHook {
    private Stats stats;
    private String namespace;
    private static final String METRICS_NAME_RETURN_TYPE = MetricsNameBuilder.builder()
            .setMetricsType(MetricsType.COUNTER)
            .setType("rocketmq")
            .setSubType("consumer")
            .setName("returnType")
            .build();
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
        final Map<String, String> props = context.getProps();
        final String returnType = props.getOrDefault(MixAll.CONSUME_CONTEXT_TYPE, ConsumeReturnType.FAILED.name());
        if (StringUtils.equals(returnType, ConsumeReturnType.EXCEPTION.name())) {
            stats.error(tags);
        }
        Counter.builder(METRICS_NAME_RETURN_TYPE)
                .tags(Tags.of(tags).and("returnType", returnType))
                .register(MetricsRegistry.getREGISTRY())
                .increment();
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

    public static void main(String[] args) {
//        Counter.builder(metricsName).tag(Tags.empty().).register(null);
//        Counter.builder(metricsName, obj, AtomicDouble::get).tags(tagsFuc.tags()).register(getRegistry());
//        System.out.println(metricsName);
    }
}
