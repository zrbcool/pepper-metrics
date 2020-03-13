package com.pepper.metrics.core.extension;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExtensionOrder {
    int value() default 127;
}
