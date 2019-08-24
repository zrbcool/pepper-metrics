package com.pepper.metrics.sample.dubbo.spring.provider;

import com.pepper.metrics.sample.dubbo.spring.api.DemoService;

/**
 * Description:
 *  引用官方示例
 * @author zhiminxu
 * @version 2019-08-15
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
