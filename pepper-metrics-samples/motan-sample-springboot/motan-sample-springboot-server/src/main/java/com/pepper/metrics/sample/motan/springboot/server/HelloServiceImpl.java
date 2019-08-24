package com.pepper.metrics.sample.motan.springboot.server;

import com.pepper.metrics.sample.motan.springboot.api.HelloService;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-14
 */
@MotanService(export = "demoMotan:8002")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        int waitTime = ThreadLocalRandom.current().nextInt(10);
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello " + name + " ! Wait time is " + waitTime;
    }
}
