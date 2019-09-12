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

#### Step2：为Motan的Protocol配置Filter

可在 `Provider` 端和 `Consumer` 端配置。

Filter名称：pepperProfiler

```java
ProtocolConfigBean config = new ProtocolConfigBean();
config.setFilter("pepperProfiler");
```

剩下的工作是日志配置，同 `Http Quick Start`。

### Redis Quick Start

适用于Jedis客户端。

#### Step1：添加依赖

```xml
<dependency>
    <groupId>com.pepper</groupId>
    <artifactId>pepper-metrics-jedis</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

#### Step2：使用Pepper提供的包装类

* Jedis客户端

Pepper为原有的Jedis API封装了代理，需要使用Pepper提供的 `PjedisPool` 构建pool。例如：

```java
public static void testJedis() throws InterruptedException {
    log.info("testJedis()");
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxTotal(300);
    config.setMaxIdle(10);
    config.setMinIdle(5);
    config.setMaxWaitMillis(6000);
    config.setTestOnBorrow(false);
    config.setTestOnReturn(false);
    config.setTestWhileIdle(true);
    config.setTestOnCreate(false);

    log.info("init JedisPoolConfig: {}", config.toString());
    // 使用PjedisPool获取Redis连接池
    PjedisPool jedisPool = new PjedisPool(config, "192.168.100.221", 6379, "somens");

    for (int j = 0; j < 100; j++) {
        for (int i = 0; i < 10; i++) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set("hello", "robin");
            }
        }
        log.info(String.format("%s NumActive:%s NumIdle:%s", j, jedisPool.getNumActive(), jedisPool.getNumIdle()));
        TimeUnit.SECONDS.sleep(1);
    }
}
```

* Jedis-Cluster客户端

```java
private static void testJedisCluster() throws InterruptedException {
    log.info("testJedisCluster()");
    String address = "192.168.100.180:9700,192.168.100.180:9701,192.168.100.180:9702,192.168.100.180:9703,192.168.100.180:9704,192.168.100.180:9705";

    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(300);
    jedisPoolConfig.setMaxIdle(10);
    jedisPoolConfig.setMinIdle(5);
    jedisPoolConfig.setMaxWaitMillis(6000);
    jedisPoolConfig.setTestOnBorrow(false);
    jedisPoolConfig.setTestOnReturn(false);
    jedisPoolConfig.setTestWhileIdle(true);
    jedisPoolConfig.setTestOnCreate(false);

    String[] commonClusterRedisArray = address.split(",");
    Set<HostAndPort> jedisClusterNodes = new HashSet<>();
    for (String clusterHostAndPort : commonClusterRedisArray) {
        String host = clusterHostAndPort.split(":")[0].trim();
        int port = Integer.parseInt(clusterHostAndPort.split(":")[1].trim());
        jedisClusterNodes.add(new HostAndPort(host, port));
    }
    
    // 用Pepper提供的PjedisCluster构建redis cluster
    PjedisCluster jedisCluster = PjedisClusterFactory.newPjedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);

    /**
     * 重要的步骤，用PjedisClusterFactory.decorateJedisCluster()包装jedisCluster即可拥有pepper-metrics-jedis的metrics能力
     * 第二个参数是namespace，当应用需要连接多组redis集群时用于区分，如果只连接一组，可以不传，默认值是default
     */
    for (int i = 0; i < 100; i++) {
        for (int j = 0; j < 10; j++) {
            jedisCluster.set("hello:"+j, "robin");
        }
        for (Map.Entry<String, JedisPool> entry : jedisCluster.getClusterNodes().entrySet()) {
            log.info(String.format("%s %s NumActive:%s NumIdle:%s", i, entry.getKey(), entry.getValue().getNumActive(), entry.getValue().getNumIdle()));
        }
        log.info("------------------------------------------------------------");
        TimeUnit.SECONDS.sleep(1);
    }
}
```
剩下的工作是日志配置，同 `Http Quick Start`。

## Pepper-Metrics-Printer插件集成原理

`Pepper-Metrics` 整体基于SPI插件机制开发，`Pepper-Metrics-Printer` 也作为 `Pepper-Metrics` 的一个插件集成到Pepper中。

在 `Pepper-Metrics` 的核心启动类 `pepper-metrics-core/src/main/java/com/pepper/metrics/core/Profiler.java` 的初始化阶段，会扫描SPI接口 `com.pepper.metrics.core.ScheduledRun` 所有的实现，`Pepper-Metrics-Printer` 就作为这个接口的实现而存在。

### 定义Pepper-Metrics-Printer插件

在 `META-INF/services` 中添加名为 `com.pepper.metrics.core.ScheduledRun` 的文件（同SPI接口名），内容为此接口的实现类：

```text
com.pepper.metrics.extension.scheduled.ScheduledPrinter
```

这样一来就可以被Profiler扫描并执行。

### 架构

`Pepper-Metrics-Printer` 实际上也是一个插件，SPI接口为 `com.pepper.metrics.extension.scheduled.PerfPrinter`， `Pepper-Metrics-Printer` 只提供通用的日志打印，具体需要打印哪个Integration的日志，需要由具体的插件实现决定。

目前 `pepper-metrics-integration` 下的所有集成均实现了此插件，所以可以打印各自的日志。

这个扫描过程如下所示：

![Pepper-Printer-SPI扫描](http://image.feathers.top/image/Pepper-Printer-SPI扫描.png)

`Pepper-Metrics-Printer` 的类关系图如下所示：

![pepper-printer类图](http://image.feathers.top/image/pepper-printer类图.png)

### 如何为自己的集成开发一个Printer

PerfPrinter接口提供的方法中，通常只需要自定义 `chooseStats` 方法即可。`print` 方法无需自己实现。

接口方法说明：
* chooseStats：选取需要打印的Stats，通常根据name选取，由于同一个name下可能有多个namespace，所以这里会返回一个数组。数组中的元素通常具有相同的name属性，但具备不同的namespace属性。
* setPrefix：定义日志前缀，继承AbstractPerfPrinter后，具备默认实现。默认实现前缀格式：perf-[type]-[namespace]
* setMetricsName：定义日志第一列【Metrics】的名称格式

setPrefix即定义的下图位置的内容：
![setPrefix说明](http://image.feathers.top/image/setPrefix说明.png)

setMetricsName定义的是下图位置的内容：
![setMetricsName说明](http://image.feathers.top/image/setMetricsName说明.png)

**下面以pepper-metrics-dubbo为例，说明如何开发一个printer**


首先在pom文件中需要添加依赖：

```xml
<dependency>
    <groupId>com.pepper</groupId>
    <artifactId>pepper-metrics-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.pepper</groupId>
    <artifactId>pepper-metrics-scheduled-printer</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

然后在 `META-INF/services` 中添加文件 `com.pepper.metrics.extension.scheduled.PerfPrinter`，内容为这个SPI的实现类：

```text
com.pepper.metrics.integration.dubbo.printer.DubboRequestInPrinter
com.pepper.metrics.integration.dubbo.printer.DubboRequestOutPrinter
```

这样一来Printer就能扫描到这两个实现了。

最后开发这两个实现类即可。以DubboRequestInPrinter为例：

```java
@SpiMeta(name = "dubboRequestInPrinter")
public class DubboRequestInPrinter extends AbstractPerfPrinter implements PerfPrinter {
    @Override
    public List<Stats> chooseStats(Set<Stats> statsSet) {
        List<Stats> statsList = new ArrayList<>();
        for (Stats stats : statsSet) {
            if (stats.getName().equalsIgnoreCase("app.dubbo.request.in")) {
                statsList.add(stats);
            }
        }
        return statsList;
    }

    @Override
    public String setMetricsName(Stats stats, List<String> tags) {
        return tags.get(3) + "." + tags.get(1);
    }
}
```

`chooseStats` 方法用于筛选 `Profiler.Builder` 的name为 `app.dubbo.request.in` 的 Stats，然后就可以只打印 `app.dubbo.request.in` 的指标，而不会将 `http` 等其他的指标打印出来了。