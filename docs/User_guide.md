# User Guide
## Samples
样例项目下载：
```bash
git clone https://github.com/zrbcool/pepper-metrics.git
cd pepper-metrics/pepper-metrics-samples
```
## 快速导航
> 简单来说使用Pepper Metrics需要进行log日志配置，根据实际情况选用不同的集成插件，两部分组成。  
> 一般来说直接使用社区提供的各种组件的集成即可，也许有某些组件不是特别流行，或者社区还没有推出集成，大家可以根据Core的使用方式，及参考插件代码自行开发扩展，这里也欢迎大家开发自己的扩展并积极推送给社区，一起完善项目。

日志配置：
- [Log](User_guide.md#log)  

各种开源组件集成索引：
- [Jedis](User_guide.md#jedis-integration)
- [Mybatis](User_guide.md#mybatis-integration)

core使用及插件开发基础：
- [Core](User_guide.md#core-use-case)
### log配置
Pepper Metrics中日志打印部分仅依赖slf4j门面库，未依赖任何具体日志实现，但是所有的sample中均以log4j2作为默认配置，这里也以log4j2为例，如果读者使用其他日志组件，请对配置做相应转换即可  
- 引入log4j2实现依赖：
```xml
<dependencies>
    <!-- log4j2 2.X -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>${log4j2.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>${log4j2.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>${log4j2.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-jcl</artifactId>
        <version>${log4j2.version}</version>
    </dependency>
</dependencies>
```
- 配置log4j2.xml文件，片段如下：
```xml
    <loggers>
        ...
        <Logger name="performance" level="INFO" additivity="true"/>
        ...
    </loggers>
```
- performance日志的log4j2的输出参考格式PATTERN：
由于性能日志有一定格式且行比较宽，所以有必要合理设置log4j去掉多余的显示，这里以log4j2设置为例
```xml
<property name="PATTERN">%d{HH:mm:ss} - %msg%xEx%n</property>
```
### jedis integration
sample项目请参考: [jedis-sample-jvm](../pepper-metrics-samples/jedis-sample-jvm)  
pom中添加如下依赖
```xml
<dependencies>
    <!-- pepper-metrics-jedis dependency -->
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-jedis</artifactId>
        <version>1.0.0</version>
    </dependency>
    <!-- pepper-metrics datasource use prometheus by default -->
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
与Jedis集成（单机），具体参考[JedisSampleMain.java](../pepper-metrics-samples/jedis-sample-jvm/src/main/java/com/pepper/metrics/sample/jedis/JedisSampleMain.java)
```java
...
// 省略构建各种参数过程，与正常使用Jedis没有差异
// 只修改这一处即可
// JedisPropsHolder.NAMESPACE是设置namespace，当应用需要连接多个redis时用于区分，如果只连接一个，可以不传，默认值是default
JedisPropsHolder.NAMESPACE.set("somens");
PjedisPool jedisPool = new PjedisPool(config, "192.168.100.221", 6379);

try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("hello", "robin");
}
```

与JedisCluster集成（集群），具体参考[JedisClusterSampleMain.java](../pepper-metrics-samples/jedis-sample-jvm/src/main/java/com/pepper/metrics/sample/jediscluster/JedisClusterSampleMain.java)
```java
...
// 省略构建各种参数过程，与正常使用JedisCluster没有差异
// 只修改这一处即可，PjedisClusterFactory.newPjedisCluster(...)，PjedisCluster完全兼容JedisCluster的API
// JedisPropsHolder.NAMESPACE是设置namespace，当应用需要连接多组redis集群时用于区分，如果只连接一组，可以不传，默认值是default
JedisPropsHolder.NAMESPACE.set("somens");
PjedisCluster jedisCluster = PjedisClusterFactory.newPjedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);

jedisCluster.set("hello:"+j, "robin");
```
日志输出效果:
```bash
[perf:jedis:somens:20190814143441] - --------------------------------------------------------------------------------------------------------------------------------------------------------------
[perf:jedis:somens:20190814143441] - | Metrics                                                                       Max(ms) Concurrent     Error     Count   P90(ms)   P99(ms)  P999(ms)     Qps | 
[perf:jedis:somens:20190814143441] - | close                                                                             2.0          0         0       290       0.0       1.0       2.0     4.8 | 
[perf:jedis:somens:20190814143441] - | getClient                                                                         1.0          0         0         2       1.0       1.0       1.0     0.0 | 
[perf:jedis:somens:20190814143441] - | resetState                                                                        1.0          0         0       290       0.0       0.0       1.0     4.8 | 
[perf:jedis:somens:20190814143441] - | connect                                                                         159.4          0         0         5     159.4     159.4     159.4     0.1 | 
[perf:jedis:somens:20190814143441] - | isConnected                                                                       0.0          0         0         1       0.0       0.0       0.0     0.0 | 
[perf:jedis:somens:20190814143441] - | set                                                                             209.7          0         0       290       1.0     209.7     209.7     4.8 | 
[perf:jedis:somens:20190814143441] - | checkIsInMultiOrPipeline                                                          1.0          0         0       291       0.0       0.0       1.0     4.8 | 
[perf:jedis:somens:20190814143441] - | ping                                                                              1.0          0         0         1       1.0       1.0       1.0     0.0 | 
[perf:jedis:somens:20190814143441] - | setDataSource                                                                    22.0          0         0       290       0.0       0.0      22.0     4.8 | 
[perf:jedis:somens:20190814143441] - | getDB                                                                             2.0          0         0       291       0.0       0.0       2.0     4.8 | 
[perf:jedis:somens:20190814143441] - --------------------------------------------------------------------------------------------------------------------------------------------------------------
```
prometheus指标输出情况：
```bash
 ✗ curl localhost:9145/metrics
# HELP jedis_summary_seconds  
# TYPE jedis_summary_seconds summary
jedis_summary_seconds{method="close",namespace="somens",quantile="0.9",} 0.0
jedis_summary_seconds{method="close",namespace="somens",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="close",namespace="somens",quantile="0.999",} 0.001998848
jedis_summary_seconds{method="close",namespace="somens",quantile="0.99999",} 0.001998848
jedis_summary_seconds_count{method="close",namespace="somens",} 440.0
jedis_summary_seconds_sum{method="close",namespace="somens",} 0.025
jedis_summary_seconds{method="ping",namespace="somens",quantile="0.9",} 0.0
jedis_summary_seconds{method="ping",namespace="somens",quantile="0.99",} 0.0
jedis_summary_seconds{method="ping",namespace="somens",quantile="0.999",} 0.0
jedis_summary_seconds{method="ping",namespace="somens",quantile="0.99999",} 0.0
jedis_summary_seconds_count{method="ping",namespace="somens",} 1.0
jedis_summary_seconds_sum{method="ping",namespace="somens",} 0.0
jedis_summary_seconds{method="getDB",namespace="somens",quantile="0.9",} 0.0
jedis_summary_seconds{method="getDB",namespace="somens",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="getDB",namespace="somens",quantile="0.999",} 0.001998848
jedis_summary_seconds{method="getDB",namespace="somens",quantile="0.99999",} 0.001998848
jedis_summary_seconds_count{method="getDB",namespace="somens",} 441.0
jedis_summary_seconds_sum{method="getDB",namespace="somens",} 0.007
jedis_summary_seconds{method="connect",namespace="somens",quantile="0.9",} 0.142573568
jedis_summary_seconds{method="connect",namespace="somens",quantile="0.99",} 0.142573568
jedis_summary_seconds{method="connect",namespace="somens",quantile="0.999",} 0.142573568
jedis_summary_seconds{method="connect",namespace="somens",quantile="0.99999",} 0.142573568
jedis_summary_seconds_count{method="connect",namespace="somens",} 5.0
jedis_summary_seconds_sum{method="connect",namespace="somens",} 0.144
jedis_summary_seconds{method="isConnected",namespace="somens",quantile="0.9",} 9.8304E-4
jedis_summary_seconds{method="isConnected",namespace="somens",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="isConnected",namespace="somens",quantile="0.999",} 9.8304E-4
jedis_summary_seconds{method="isConnected",namespace="somens",quantile="0.99999",} 9.8304E-4
jedis_summary_seconds_count{method="isConnected",namespace="somens",} 1.0
jedis_summary_seconds_sum{method="isConnected",namespace="somens",} 0.001
jedis_summary_seconds{method="setDataSource",namespace="somens",quantile="0.9",} 0.0
jedis_summary_seconds{method="setDataSource",namespace="somens",quantile="0.99",} 0.0
jedis_summary_seconds{method="setDataSource",namespace="somens",quantile="0.999",} 0.013074432
jedis_summary_seconds{method="setDataSource",namespace="somens",quantile="0.99999",} 0.013074432
jedis_summary_seconds_count{method="setDataSource",namespace="somens",} 440.0
jedis_summary_seconds_sum{method="setDataSource",namespace="somens",} 0.015
jedis_summary_seconds{method="checkIsInMultiOrPipeline",namespace="somens",quantile="0.9",} 0.0
jedis_summary_seconds{method="checkIsInMultiOrPipeline",namespace="somens",quantile="0.99",} 0.0
jedis_summary_seconds{method="checkIsInMultiOrPipeline",namespace="somens",quantile="0.999",} 9.8304E-4
jedis_summary_seconds{method="checkIsInMultiOrPipeline",namespace="somens",quantile="0.99999",} 9.8304E-4
jedis_summary_seconds_count{method="checkIsInMultiOrPipeline",namespace="somens",} 441.0
jedis_summary_seconds_sum{method="checkIsInMultiOrPipeline",namespace="somens",} 0.001
jedis_summary_seconds{method="set",namespace="somens",quantile="0.9",} 9.8304E-4
jedis_summary_seconds{method="set",namespace="somens",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="set",namespace="somens",quantile="0.999",} 0.209682432
jedis_summary_seconds{method="set",namespace="somens",quantile="0.99999",} 0.209682432
jedis_summary_seconds_count{method="set",namespace="somens",} 440.0
jedis_summary_seconds_sum{method="set",namespace="somens",} 0.609
jedis_summary_seconds{method="getClient",namespace="somens",quantile="0.9",} 9.8304E-4
jedis_summary_seconds{method="getClient",namespace="somens",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="getClient",namespace="somens",quantile="0.999",} 9.8304E-4
jedis_summary_seconds{method="getClient",namespace="somens",quantile="0.99999",} 9.8304E-4
jedis_summary_seconds_count{method="getClient",namespace="somens",} 2.0
jedis_summary_seconds_sum{method="getClient",namespace="somens",} 0.001
jedis_summary_seconds{method="resetState",namespace="somens",quantile="0.9",} 0.0
jedis_summary_seconds{method="resetState",namespace="somens",quantile="0.99",} 0.0
jedis_summary_seconds{method="resetState",namespace="somens",quantile="0.999",} 9.8304E-4
jedis_summary_seconds{method="resetState",namespace="somens",quantile="0.99999",} 9.8304E-4
jedis_summary_seconds_count{method="resetState",namespace="somens",} 440.0
jedis_summary_seconds_sum{method="resetState",namespace="somens",} 0.003
# HELP jedis_summary_seconds_max  
# TYPE jedis_summary_seconds_max gauge
jedis_summary_seconds_max{method="close",namespace="somens",} 0.002
jedis_summary_seconds_max{method="ping",namespace="somens",} 0.0
jedis_summary_seconds_max{method="getDB",namespace="somens",} 0.002
jedis_summary_seconds_max{method="connect",namespace="somens",} 0.142
jedis_summary_seconds_max{method="isConnected",namespace="somens",} 0.001
jedis_summary_seconds_max{method="setDataSource",namespace="somens",} 0.013
jedis_summary_seconds_max{method="checkIsInMultiOrPipeline",namespace="somens",} 0.001
jedis_summary_seconds_max{method="set",namespace="somens",} 0.208
jedis_summary_seconds_max{method="getClient",namespace="somens",} 0.001
jedis_summary_seconds_max{method="resetState",namespace="somens",} 0.001
# HELP jedis_concurrent_gauge  
# TYPE jedis_concurrent_gauge gauge
jedis_concurrent_gauge{method="close",namespace="somens",} 0.0
jedis_concurrent_gauge{method="ping",namespace="somens",} 0.0
jedis_concurrent_gauge{method="getDB",namespace="somens",} 0.0
jedis_concurrent_gauge{method="connect",namespace="somens",} 0.0
jedis_concurrent_gauge{method="isConnected",namespace="somens",} 0.0
jedis_concurrent_gauge{method="setDataSource",namespace="somens",} 0.0
jedis_concurrent_gauge{method="checkIsInMultiOrPipeline",namespace="somens",} 0.0
jedis_concurrent_gauge{method="set",namespace="somens",} 0.0
jedis_concurrent_gauge{method="getClient",namespace="somens",} 0.0
jedis_concurrent_gauge{method="resetState",namespace="somens",} 0.0
```
### mybatis integration
sample项目请参考: [mybatis-sample-springboot](../pepper-metrics-samples/mybatis-sample-springboot)  
pom中增加依赖：
```xml
<dependencies>
    <!-- pepper metrics dependencies -->
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-mybatis</artifactId>
        <version>1.0.0</version>
    </dependency>
    <!-- pepper-metrics datasource use prometheus by default -->
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
在mybatis的配置mybatis-config.xml当中增加即完成集成
```xml
<configuration>
    ...
    <plugins>
        <plugin interceptor="com.pepper.metrics.integration.mybatis.MybatisProfilerPlugin" />
    </plugins>
    ...
</configuration>
```
日志输出格式：
```bash
[perf:mybatis:20190814144344] - --------------------------------------------------------------------------------------------------------------------------------------------------------------
[perf:mybatis:20190814144344] - | Metrics                                                                       Max(ms) Concurrent     Error     Count   P90(ms)   P99(ms)  P999(ms)     Qps | 
[perf:mybatis:20190814144344] - | com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId               3.4          0         0      1950       0.7       1.7       3.3    32.5 | 
[perf:mybatis:20190814144344] - | sample.mybatis.mapper.CityMapper.selectCityById                                  58.7          0         0      1950       0.7       2.4      58.7    32.5 | 
[perf:mybatis:20190814144344] - --------------------------------------------------------------------------------------------------------------------------------------------------------------
```
prometheus指标输出情况：与其他相似，只是指标名区别

### Dubbo integration
sample项目请参考：[dubbo-sample-spring](https://github.com/Lord-X/pepper-metrics/tree/master/pepper-metrics-samples/dubbo-sample-spring)

pom中添加依赖即可:

```xml
<dependencies>
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-dubbo</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### http integration
sample项目请参考：[servlet-sample-springboot](https://github.com/Lord-X/pepper-metrics/tree/master/pepper-metrics-samples/servlet-sample-springboot)

pom中添加依赖：
```xml
<dependencies>
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-servlet</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
* 在web.xml或springboot的Configuration中配置PerfFilter，以SpringBoot为例：
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

### motan integration
sample项目请参考：[motan-sample-jvm](https://github.com/Lord-X/pepper-metrics/tree/master/pepper-metrics-samples/motan-sample-jvm)，[motan-sample-springboot](https://github.com/Lord-X/pepper-metrics/tree/master/pepper-metrics-samples/motan-sample-springboot)

在pom中添加依赖：

```xml
<dependencies>
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-motan</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>top.pepperx</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

给Motan的ProtocolConfig添加Filter配置，名称为"pepperProfiler"，以SpringBoot为例：
```java
ProtocolConfigBean config = new ProtocolConfigBean();
config.setDefault(true);
config.setName("motan");
config.setMaxContentLength(1048576);
// 添加Filter
config.setFilter("pepperProfiler");
return config;
```
这里注意，Filter的名称一定为pepperProfiler。可在Server端和Client端同时添加。


### core use case
一般应用都是直接使用各种开源组件的集成，如果有特殊需要，例如需要有自定义的性能收集或者开发扩展插件时才需要了解core的使用，这里简单介绍，详细了解，请查看各个插件的使用方式，参考链接：[pepper-metrics-integration](../pepper-metrics-integration)
- 性能收集代码使用样例
```java
public class CoreSampleMain {
    public static void main(String[] args) {
        final Stats stats = Profiler.Builder
                .builder()
                .name("custom")
                .namespace("myns")
                .build();
        String[] tags = new String[]{"method", "mockLatency()"};
        for (int i = 0; i < 10; i++) {
            stats.incConc(tags);
            long begin = System.nanoTime();
            try {
                mockLatency();
            } catch (Exception e) {
                stats.error(tags);
            } finally {
                stats.observe(System.nanoTime() - begin, TimeUnit.NANOSECONDS, tags);
                stats.decConc(tags);
            }
        }
    }
    
    private static void mockLatency() {
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(50, 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
- 如果收集的指标需要输出perf日志，则需要开发一个printer插件
```java
@SpiMeta(name = "coreSamplePrinter")
public class CoreSamplePrinter extends AbstractPerfPrinter {
    @Override
    public List<Stats> chooseStats(Set<Stats> statsSet) {
        List<Stats> statsList = new ArrayList<>();
        for (Stats stats : statsSet) {
            if ("custom".equalsIgnoreCase(stats.getName())) {
                statsList.add(stats);
            }
        }
        return statsList;
    }
}
```  
同时配置SPI使其能被ExtensionLoader发现并加载，完整代码请参考sample项目：[core-sample-jvm](../pepper-metrics-samples/core-sample-jvm)




