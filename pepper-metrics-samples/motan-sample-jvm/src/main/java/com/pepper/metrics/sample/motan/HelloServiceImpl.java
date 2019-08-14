package com.pepper.metrics.sample.motan;

/**
 * Description:
 *
 * @author zhiminxu
 * @package com.pepper.metrics.sample.motan
 * @create_time 2019-08-14
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String name) {
        System.out.println("Hello " + name + " !");
    }
}
