package com.pepper.metrics.integration.rocketmq.deprecated;

import com.pepper.metrics.core.extension.ExtensionLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

/**
 * @author zhangrongbincool@163.com
 * @version 20-1-2
 */
@Deprecated
public class DefaultMQProducerFactory {
    private static String fixNamespace() {
        String namespace = "default";
        if (StringUtils.isNotEmpty(MQPropsHolder.NAMESPACE.get())) {
            namespace = MQPropsHolder.NAMESPACE.get();
        }
        return namespace;
    }

    public static DefaultMQProducer newDefaultMQProducer() {
        return ExtensionLoader.getExtensionLoader(DefaultMQProducerProxyFactory.class)
                .getExtension("cglib")
                .getProxy(DefaultMQProducer.class, fixNamespace());
    }

    private static DefaultMQProducer newDefaultMQProducer(Class[] classes, Object[] objects) {
        //        JedisClusterHealthTracker.addJedisCluster(fixNamespace(), jedisCluster);
        return ExtensionLoader.getExtensionLoader(DefaultMQProducerProxyFactory.class)
                .getExtension("cglib")
                .getProxy(DefaultMQProducer.class, fixNamespace(), classes, objects);
    }
}
