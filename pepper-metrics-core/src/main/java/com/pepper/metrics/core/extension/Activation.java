package com.pepper.metrics.core.extension;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Activation {

    /** seq号越小，在返回的list<Instance>中的位置越靠前，尽量使用 0-100以内的数字 */
    int sequence() default 20;

    /** spi 的key，获取spi列表时，根据key进行匹配，当key中存在待过滤的search-key时，匹配成功 */
    String[] key() default "";

}
