package com.pepper.metrics.sample.servlet.controller;

import com.pepper.metrics.integration.custom.CustomProfiler;
import com.pepper.metrics.sample.servlet.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-13
 */
@RestController
@RequestMapping("/perf")
public class PerfController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("/trace")
    public Object trace() {
        int randomTime = 0;
        final CustomProfiler.Procedure procedure = CustomProfiler.beginProcedure();
        try {
            helloService.hello();
            randomTime = ThreadLocalRandom.current().nextInt(1000);
            Thread.sleep(randomTime);
        } catch (Exception e) {
            try {
                procedure.exception(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } finally {
            procedure.complete();
        }
        return "ok - execution time: " + randomTime;
    }
}
