package com.pepper.metrics.sample.dubbo.spring.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-15
 */
public class Provider {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-demo-provider.xml");
        context.start();

        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }
}
