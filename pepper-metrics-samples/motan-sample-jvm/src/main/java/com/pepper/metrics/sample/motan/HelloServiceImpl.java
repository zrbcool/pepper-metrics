package com.pepper.metrics.sample.motan;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-14
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String name) {
        int waitTime = ThreadLocalRandom.current().nextInt(1000);
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Hello " + name + " ! Wait time is " + waitTime);
    }
}
