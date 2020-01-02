package com.pepper.metrics.integration.rocketmq;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-16
 */
public class MQPropsHolder {
    public static final ThreadLocal<String> NAMESPACE = new ThreadLocal<>();
}
