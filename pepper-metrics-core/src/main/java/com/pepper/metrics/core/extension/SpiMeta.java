package com.pepper.metrics.core.extension;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpiMeta {
    String name() default "";
}
