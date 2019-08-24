package com.pepper.metrics.sample.motan.springboot.client.controller;

import com.pepper.metrics.sample.motan.springboot.api.HelloService;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-14
 */
@RestController
@RequestMapping("/motan")
public class MotanController {

    @MotanReferer(basicReferer = "motantestClientBasicConfig", group = "testgroup", directUrl = "127.0.0.1:8002")
    private HelloService service;

    @RequestMapping("/trace")
    public Object trace() {
        for (int i=0;i<100;i++) {
            String name = service.sayHello("hahaha" + i);
            System.out.println(name);
        }

        return "OK";
    }
}
