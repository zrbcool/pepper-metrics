package com.pepper.metrics.sample.rocketmq.service;

import com.pepper.metrics.integration.rocketmq.perf.ConsumerConsumeMessageHook;
import com.pepper.metrics.integration.rocketmq.perf.ProducerSendMessageHook;
import com.pepper.metrics.integration.rocketmq.health.RocketMQHealthTracker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-2
 */
@Slf4j
@Component
public class RMQRunner implements ApplicationRunner {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    @Override
    public void run(ApplicationArguments args) throws MQClientException {
        String NAME_SRV_ADDR = "rocketmq-c0-namesrv001.coohua-inc.com:9876;rocketmq-c0-namesrv002.coohua-inc.com:9876";
        final String topic = "PEPPER-TEST-TOPIC";
        final String namespace = "default";

        //DefaultMQProducer producer = DefaultMQProducerFactory.newDefaultMQProducer();
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.getDefaultMQProducerImpl().registerSendMessageHook(new ProducerSendMessageHook(namespace));
        producer.setNamesrvAddr(NAME_SRV_ADDR);
        producer.setProducerGroup("default");
        producer.start();

        EXECUTOR.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                try {
                    Message msg = new Message(topic /* Topic */,
                            ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
                    );
                    producer.send(msg);
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (UnsupportedEncodingException | InterruptedException | RemotingException | MQClientException | MQBrokerException e) {
                    log.error("", e);
                }
            }
        });

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(NAME_SRV_ADDR);
        consumer.subscribe(topic, "*");
        final String consumerGroup = "default";
        consumer.setConsumerGroup(consumerGroup);
        final MessageListenerConcurrently listener = (msgs, context) -> {
            for (MessageExt msg : msgs) {
//                log.info(msg.toString());
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
        //consumer.setMessageListener(RocketMQHealthTracker.proxy(namespace, consumerGroup, listener));
        consumer.setMessageListener(listener);
        consumer.getDefaultMQPushConsumerImpl().registerConsumeMessageHook(new ConsumerConsumeMessageHook(namespace));
        consumer.start();
        RocketMQHealthTracker.addDefaultMQPushConsumer(namespace, consumer);

    }

}
