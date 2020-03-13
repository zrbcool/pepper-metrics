package com.pepper.metrics.integration.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD
})
public @interface Profile {
    ProfileType type() default ProfileType.LATENCY;
}
