package com.pepper.metrics.sample.motan.springboot.server;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-14
 */
@SpringBootApplication
public class MotanServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotanServerApplication.class, args);
        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
    }

    @Bean
    public AnnotationBean motanAnnotationBean() {
        AnnotationBean motanAnnotationBean = new AnnotationBean();
        motanAnnotationBean.setPackage("com.pepper.metrics.sample.motan.springboot.server");
        return motanAnnotationBean;
    }
}
