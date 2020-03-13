package com.pepper.metrics.integration.mybatis;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-12
 */
@Intercepts(
    value = {
        @Signature (type=Executor.class,
                method="update",
                args={MappedStatement.class,Object.class}),
        @Signature(type=Executor.class,
                method="query",
                args={MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class,
                        CacheKey.class,BoundSql.class}),
        @Signature(type=Executor.class,
                method="query",
                args={MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class})
    }
)
public class MybatisProfilerPlugin implements Interceptor {
    private static final Stats MYBATIS_STAT = Profiler.Builder
            .builder()
            .type("mybatis")
            .build();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        if (!System.getProperty("mybatisProfileEnable", "true").equalsIgnoreCase("true")) {
            return invocation.proceed();
        }
        final Object[] args = invocation.getArgs();
        if (args != null && args.length > 0) {
            long begin = System.nanoTime();
            final MappedStatement mappedStatement = (MappedStatement) args[0];
            if (mappedStatement != null) {
                final String methodName = mappedStatement.getId();
                final String declaringTypeName = mappedStatement.getResource();
                String[] tags = new String[]{"operation", methodName, "class", declaringTypeName};
                try {
                    MYBATIS_STAT.incConc(tags);
                    return invocation.proceed();
                } catch (Throwable throwable) {
                    MYBATIS_STAT.error(tags);
                    throw throwable;
                } finally {
                    MYBATIS_STAT.decConc(tags);
                    MYBATIS_STAT.observe(System.nanoTime() - begin, TimeUnit.NANOSECONDS, tags);
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}