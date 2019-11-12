package com.pepper.metrics.integration.custom;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static com.pepper.metrics.integration.custom.CustomProfiler.CUSTOMIZED_STAT;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
@Aspect
public class CustomProfilerAspect {

    @Pointcut("@annotation(com.pepper.metrics.integration.custom.Profile)")
    public void pointcut(){}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final String metrics = joinPoint.getSignature().toShortString();
        final String category = joinPoint.getSignature().getDeclaringType().getSimpleName();
        final String[] tags = new String[]{"operation", metrics, "class", category};
        long beginTime = System.currentTimeMillis();
        try {
            CUSTOMIZED_STAT.incConc(tags);
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            CUSTOMIZED_STAT.error(tags);
            throw throwable;
        } finally {
            CUSTOMIZED_STAT.observe(System.currentTimeMillis() - beginTime, tags);
            CUSTOMIZED_STAT.decConc(tags);
        }
    }
}
