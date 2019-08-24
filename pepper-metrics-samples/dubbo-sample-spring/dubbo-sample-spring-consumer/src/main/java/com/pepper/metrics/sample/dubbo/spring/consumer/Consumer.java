package com.pepper.metrics.sample.dubbo.spring.consumer;

import com.pepper.metrics.sample.dubbo.spring.api.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-15
 */
public class Consumer {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-demo-consumer.xml");
        context.start();
        DemoService demoService = (DemoService) context.getBean("demoService");
        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (true) {
            Thread.sleep(random.nextLong(1000));
            String hello = demoService.sayHello("world");
            System.out.println(hello);
        }
    }
}
