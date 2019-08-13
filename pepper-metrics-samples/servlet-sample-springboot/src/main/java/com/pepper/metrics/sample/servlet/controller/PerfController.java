package com.pepper.metrics.sample.servlet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Description:
 *
 * @author zhiminxu
 * @package com.pepper.metrics.sample.servlet.controller
 * @create_time 2019-08-13
 */
@RestController
@RequestMapping("/perf")
public class PerfController {

    @RequestMapping("/trace")
    public Object trace() {
        int randomTime = ThreadLocalRandom.current().nextInt(1000);
        try {
            Thread.sleep(randomTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "ok - execution time: " + randomTime;
    }
}
