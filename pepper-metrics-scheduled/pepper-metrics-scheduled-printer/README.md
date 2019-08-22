# Pepper-Printer：Performance日志打印插件

## 介绍

Pepper-Printer可将Pepper-Metrics收集的应用性能指标取出来，并打印在指定的日志输出中，1分钟打印一次。

Pepper-Printer本身只提供了slf4j的日志接口，具体的日志实现使用用户引用的具体第三方日志组件，如：log4j、log4j2、logback等。

目前已经支持了 `pepper-metrics-integration` 包中的所有集成，日志打印效果如下：

> 以下日志打印维度说明：
> * Metrics：接口名。如果是http，这里是从根路径开始的URL，如果是Redis，这里是具体的方法名。
> * Concurrent：并发数
> * Count(Err/Sum)：总计(错误数/请求总数)
> * P90：%90请求的响应时间（毫秒）
> * P99：%99请求的响应时间（毫秒）
> * P999：%99.9请求的响应时间（毫秒）
> * Max：最大接口响应时间（毫秒）
> * QPS：1分钟内平均每秒的请求数

* http指标：
![pepper-printer-http](http://image.feathers.top/image/pepper-printer-http.png)

* 单点Redis指标：
![pepper-printer-single-redis](http://image.feathers.top/image/pepper-printer-single-redis.png)

* Redis-Cluster指标：
![pepper-printer-redis-cluster](http://image.feathers.top/image/pepper-printer-redis-cluster.png)

* Motan-Provider指标：
![pepper-printer-motan-provider](http://image.feathers.top/image/pepper-printer-motan-provider.png)

* Motan-Consumer指标：
![pepper-printer-motan-consumer](http://image.feathers.top/image/pepper-printer-motan-consumer.png)

* MyBatis指标：
![pepper-printer-mybatis](http://image.feathers.top/image/pepper-printer-mybatis.png)

## Quick Start

### Http Quick Start

#### Step1：Maven中依赖需要的jar包

想收集哪个维度的性能指标，就依赖哪个integration。具体可以参考 `pepper-metrics-samples` 下的示例。

比如，我想收集并打印http接口的性能指标，那么只需要添加 `pepper-metrics-servlet` 的依赖即可（可参考项目：[servlet-sample-springboot](https://github.com/zrbcool/pepper-metrics/tree/master/pepper-metrics-samples/servlet-sample-springboot)）。

```xml
<dependency>
    <groupId>com.pepper</groupId>
    <artifactId>pepper-metrics-servlet</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### Step2：配置Filter

Pepper-Metrics通过 `PerfFilter` 收集性能信息。所以需要配置在Filter链中。例如，基于SpringBoot的项目配置如下：

```java
@Configuration
@ConditionalOnClass(HttpServletRequest.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication
public class WebAutoConfig {

    @Bean
    public FilterRegistrationBean profilerFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PerfFilter());
        registration.addUrlPatterns("/*");
        registration.setName("profilerHttpFilter");
        registration.setOrder(1);

        return registration;
    }
}
```

#### Step3：添加日志实现
添加具体的第三方日志实现依赖，例如log4j2：

```xml
<!-- log4j2 2.X -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-jcl</artifactId>
    <version>2.2</version>
</dependency>
```

#### Step4：配置日志

这里需要注意，必须要有一个名为 `performance` 的Logger。例如：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration status="off" monitorInterval="30">
    <properties>
        <property name="PATTERN">%msg%xEx%n</property>
        <property name="OUTPUT_LOG_LEVEL">info</property><!-- 日志输出级别 -->
    </properties>
    <appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="${PATTERN}"/>
        </Console>
    </appenders>

    <loggers>
        <!-- Printer logger配置，打印到root的console中 -->
        <Logger name="performance" level="${OUTPUT_LOG_LEVEL}" additivity="true"/>

        <ROOT level="${OUTPUT_LOG_LEVEL}">
            <AppenderRef ref="Console"/>
        </ROOT>
    </loggers>
</configuration>
```

That's all！需要额外说明的是，日志最终打印格式依赖于这里配置的pattern。推荐：`%msg%xEx%n`。

### MyBatis Quick Start

#### Step1：添加依赖

```xml
<dependency>
    <groupId>com.pepper</groupId>
    <artifactId>pepper-metrics-mybatis</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### Step2：配置MyBatis插件

在 `mybatis-config.xml` 中添加 Pepper-Metrics 提供的MyBatis插件：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <typeAliases>
        <package name="com.pepper.metrics.sample.mybatis.domain"/>
    </typeAliases>
    <plugins>
        <!-- Here is -->
        <plugin interceptor="com.pepper.metrics.integration.mybatis.MybatisProfilerPlugin" />
    </plugins>
    <mappers>
        <mapper resource="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml"/>
        <mapper resource="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml"/>
    </mappers>
</configuration>
```

剩下的工作是日志配置，同 `Http Quick Start`。

### Dubbo Quick Start

#### Step1：添加依赖

```xml
<dependency>
    <groupId>com.pepper</groupId>
    <artifactId>pepper-metrics-dubbo</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

剩下的工作是日志配置，同 `Http Quick Start`。

### Motan Quick Start

#### Step1：添加依赖

```xml
<dependency>
    <groupId>com.pepper</groupId>
    <artifactId>pepper-metrics-motan</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### Step：为Motan的Protocol配置Filter

可在 `Provider` 端和 `Consumer` 端配置。

Filter名称：pepperProfiler

```java
ProtocolConfigBean config = new ProtocolConfigBean();
config.setFilter("pepperProfiler");
```

剩下的工作是日志配置，同 `Http Quick Start`。

