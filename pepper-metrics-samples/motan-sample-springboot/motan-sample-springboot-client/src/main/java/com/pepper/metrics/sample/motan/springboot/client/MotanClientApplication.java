package com.pepper.metrics.sample.motan.springboot.client;

import com.pepper.metrics.sample.motan.springboot.api.HelloService;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-14
 */
@SpringBootApplication
public class MotanClientApplication {

    @MotanReferer(basicReferer = "motantestClientBasicConfig", group = "testgroup", directUrl = "127.0.0.1:8002")
    private HelloService service;

    @Scheduled(cron = "0/1 * * * * ?")
    public void timer() {
        String name = service.sayHello("hahaha");
        System.out.println(name);
    }

    public static void main(String[] args) {
        SpringApplication.run(MotanClientApplication.class, args);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public AnnotationBean motanAnnotationBean() {
        AnnotationBean motanAnnotationBean = new AnnotationBean();
        motanAnnotationBean.setPackage("com.pepper.metrics.sample.motan.springboot.client");
        return motanAnnotationBean;
    }

}
