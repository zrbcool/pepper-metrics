# Pepper-Metrics-Servlet集成原理

## 1 目的

Pepper需要统计http层面web接口的响应性能，固需要计算请求前后的响应时间、并发数、错误数、请求数等profile。

## 2 集成原理

本集成的核心实现为 `com.pepper.metrics.integration.servlet.PerfFilter` ，他是一个Servlet容器标准的Filter，实现了 `javax.servlet.Filter` 接口。

他作为一个过滤器存在于应用程序中，用户需要手动将 `PerfFilter` 配置到 `web.xml` 中。

`PerfFilter` 中定义了 `Profiler` 的静态变量，用于在类加载的时候初始化 `Profiler`，这样一来，只要将 `PerfFilter` 配置到 `web.xml` 中，`Profiler` 就自动启动了。

最终，`PerfFilter.doFilter()` 方法中，在请求前后完成了响应时间、并发数、错误数、请求数等的收集。