package com.pepper.metrics.sample.servlet.service;

import com.pepper.metrics.integration.custom.Profile;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-12
 */
@Component
public class HelloService {
    @Profile
    public void hello() {
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(50, 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
