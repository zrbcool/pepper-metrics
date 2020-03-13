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
- [Motan](User_guide.md#motan-integration)
- [Http / Servlet](User_guide.md#http-integration)
- [Dubbo](User_guide.md#dubbo-integration)
- [Druid](User_guide.md)



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
- 一个完整的log4j2.xml例子请参考[log4j2.xml](../../pepper-metrics-samples/jedis-sample-jvm/src/main/resources/log4j2.xml)
### jedis integration
sample项目请参考: [jedis-sample-jvm](../../pepper-metrics-samples/jedis-sample-jvm)  
pom中添加如下依赖
```xml
<dependencies>
    <!-- pepper-metrics-jedis dependency -->
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-jedis</artifactId>
        <version>1.0.11</version>
    </dependency>
    <!-- pepper-metrics datasource use prometheus by default -->
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.11</version>
    </dependency>
</dependencies>
```
与Jedis集成（单机），具体参考[JedisSampleMain.java](../../pepper-metrics-samples/jedis-sample-jvm/src/main/java/com/pepper/metrics/sample/jedis/JedisSampleMain.java)
```java
...
// 省略构建各种参数过程，与正常使用Jedis没有差异
// 只修改这一处即可
// JedisPropsHolder.NAMESPACE是设置namespace，当应用需要连接多个redis时用于区分，如果只连接一个，可以不传，默认值是default
JedisPropsHolder.NAMESPACE.set("myns");
PjedisPool jedisPool = new PjedisPool(config, "192.168.100.221", 6379);

try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("hello", "robin");
}
```

与JedisCluster集成（集群），具体参考[JedisClusterSampleMain.java](../../pepper-metrics-samples/jedis-sample-jvm/src/main/java/com/pepper/metrics/sample/jediscluster/JedisClusterSampleMain.java)
```java
...
// 只修改这一处即可，将正常构造JedisCluster的参数传递给如下工厂方法，其支持所有jedisCluster的构造方法
// PjedisClusterFactory.newjedisCluster(...)
// JedisPropsHolder.NAMESPACE是设置namespace，当应用需要连接多组redis集群时用于区分，如果只连接一组，可以不传，默认值是default
JedisPropsHolder.NAMESPACE.set("cluster");
JedisCluster jedisCluster = PjedisClusterFactory.newJedisCluster(jedisClusterNodes, defaultConnectTimeout, defaultConnectMaxAttempts, jedisPoolConfig);

jedisCluster.set("hello:"+j, "robin");
```
日志输出效果:
```bash
17:59:07 [perf-jedis-myns:20190822175907] ---------------------------------------------------------------------------------------------------------------------------------------------------------
17:59:07 [perf-jedis-myns:20190822175907] | Metrics                                                                     Concurrent Count(Err/Sum)   P90(ms)   P99(ms)  P999(ms)   Max(ms)     Qps | 
17:59:07 [perf-jedis-myns:20190822175907] | isConnected                                                                          0            0/1       1.0       1.0       1.0       1.0     0.0 | 
17:59:07 [perf-jedis-myns:20190822175907] | connect                                                                              0            0/5     142.6     142.6     142.6     142.6     0.1 | 
17:59:07 [perf-jedis-myns:20190822175907] | getClient                                                                            0            0/2       1.0       1.0       1.0       1.0     0.0 | 
17:59:07 [perf-jedis-myns:20190822175907] | set                                                                                  0          0/300       1.0       5.2     218.1     218.1     5.0 | 
17:59:07 [perf-jedis-myns:20190822175907] | close                                                                                0          0/300       0.0       1.0       4.0       4.0     5.0 | 
17:59:07 [perf-jedis-myns:20190822175907] | setDataSource                                                                        0          0/300       0.0       1.0      19.9      19.9     5.0 | 
17:59:07 [perf-jedis-myns:20190822175907] | ping                                                                                 0            0/1       1.0       1.0       1.0       1.0     0.0 | 
17:59:07 [perf-jedis-myns:20190822175907] | checkIsInMultiOrPipeline                                                             0          0/301       0.0       0.0       1.0       1.0     5.0 | 
17:59:07 [perf-jedis-myns:20190822175907] | getDB                                                                                0          0/301       0.0       1.0       2.0       2.0     5.0 | 
17:59:07 [perf-jedis-myns:20190822175907] | resetState                                                                           0          0/300       0.0       0.0       1.0       1.0     5.0 | 
17:59:07 [perf-jedis-myns:20190822175907] ---------------------------------------------------------------------------------------------------------------------------------------------------------
```
prometheus指标输出情况：
```bash
✗ curl localhost:9145/metrics
# HELP jedis_summary_seconds  
# TYPE jedis_summary_seconds summary
jedis_summary_seconds{method="close",namespace="myns",quantile="0.9",} 0.0
jedis_summary_seconds{method="close",namespace="myns",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="close",namespace="myns",quantile="0.999",} 0.001998848
jedis_summary_seconds{method="close",namespace="myns",quantile="0.99999",} 0.001998848
jedis_summary_seconds_count{method="close",namespace="myns",} 440.0
jedis_summary_seconds_sum{method="close",namespace="myns",} 0.025
jedis_summary_seconds{method="ping",namespace="myns",quantile="0.9",} 0.0
jedis_summary_seconds{method="ping",namespace="myns",quantile="0.99",} 0.0
jedis_summary_seconds{method="ping",namespace="myns",quantile="0.999",} 0.0
jedis_summary_seconds{method="ping",namespace="myns",quantile="0.99999",} 0.0
jedis_summary_seconds_count{method="ping",namespace="myns",} 1.0
jedis_summary_seconds_sum{method="ping",namespace="myns",} 0.0
jedis_summary_seconds{method="getDB",namespace="myns",quantile="0.9",} 0.0
jedis_summary_seconds{method="getDB",namespace="myns",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="getDB",namespace="myns",quantile="0.999",} 0.001998848
jedis_summary_seconds{method="getDB",namespace="myns",quantile="0.99999",} 0.001998848
jedis_summary_seconds_count{method="getDB",namespace="myns",} 441.0
jedis_summary_seconds_sum{method="getDB",namespace="myns",} 0.007
jedis_summary_seconds{method="connect",namespace="myns",quantile="0.9",} 0.142573568
jedis_summary_seconds{method="connect",namespace="myns",quantile="0.99",} 0.142573568
jedis_summary_seconds{method="connect",namespace="myns",quantile="0.999",} 0.142573568
jedis_summary_seconds{method="connect",namespace="myns",quantile="0.99999",} 0.142573568
jedis_summary_seconds_count{method="connect",namespace="myns",} 5.0
jedis_summary_seconds_sum{method="connect",namespace="myns",} 0.144
jedis_summary_seconds{method="isConnected",namespace="myns",quantile="0.9",} 9.8304E-4
jedis_summary_seconds{method="isConnected",namespace="myns",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="isConnected",namespace="myns",quantile="0.999",} 9.8304E-4
jedis_summary_seconds{method="isConnected",namespace="myns",quantile="0.99999",} 9.8304E-4
jedis_summary_seconds_count{method="isConnected",namespace="myns",} 1.0
jedis_summary_seconds_sum{method="isConnected",namespace="myns",} 0.001
jedis_summary_seconds{method="setDataSource",namespace="myns",quantile="0.9",} 0.0
jedis_summary_seconds{method="setDataSource",namespace="myns",quantile="0.99",} 0.0
jedis_summary_seconds{method="setDataSource",namespace="myns",quantile="0.999",} 0.013074432
jedis_summary_seconds{method="setDataSource",namespace="myns",quantile="0.99999",} 0.013074432
jedis_summary_seconds_count{method="setDataSource",namespace="myns",} 440.0
jedis_summary_seconds_sum{method="setDataSource",namespace="myns",} 0.015
jedis_summary_seconds{method="checkIsInMultiOrPipeline",namespace="myns",quantile="0.9",} 0.0
jedis_summary_seconds{method="checkIsInMultiOrPipeline",namespace="myns",quantile="0.99",} 0.0
jedis_summary_seconds{method="checkIsInMultiOrPipeline",namespace="myns",quantile="0.999",} 9.8304E-4
jedis_summary_seconds{method="checkIsInMultiOrPipeline",namespace="myns",quantile="0.99999",} 9.8304E-4
jedis_summary_seconds_count{method="checkIsInMultiOrPipeline",namespace="myns",} 441.0
jedis_summary_seconds_sum{method="checkIsInMultiOrPipeline",namespace="myns",} 0.001
jedis_summary_seconds{method="set",namespace="myns",quantile="0.9",} 9.8304E-4
jedis_summary_seconds{method="set",namespace="myns",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="set",namespace="myns",quantile="0.999",} 0.209682432
jedis_summary_seconds{method="set",namespace="myns",quantile="0.99999",} 0.209682432
jedis_summary_seconds_count{method="set",namespace="myns",} 440.0
jedis_summary_seconds_sum{method="set",namespace="myns",} 0.609
jedis_summary_seconds{method="getClient",namespace="myns",quantile="0.9",} 9.8304E-4
jedis_summary_seconds{method="getClient",namespace="myns",quantile="0.99",} 9.8304E-4
jedis_summary_seconds{method="getClient",namespace="myns",quantile="0.999",} 9.8304E-4
jedis_summary_seconds{method="getClient",namespace="myns",quantile="0.99999",} 9.8304E-4
jedis_summary_seconds_count{method="getClient",namespace="myns",} 2.0
jedis_summary_seconds_sum{method="getClient",namespace="myns",} 0.001
jedis_summary_seconds{method="resetState",namespace="myns",quantile="0.9",} 0.0
jedis_summary_seconds{method="resetState",namespace="myns",quantile="0.99",} 0.0
jedis_summary_seconds{method="resetState",namespace="myns",quantile="0.999",} 9.8304E-4
jedis_summary_seconds{method="resetState",namespace="myns",quantile="0.99999",} 9.8304E-4
jedis_summary_seconds_count{method="resetState",namespace="myns",} 440.0
jedis_summary_seconds_sum{method="resetState",namespace="myns",} 0.003
# HELP jedis_summary_seconds_max  
# TYPE jedis_summary_seconds_max gauge
jedis_summary_seconds_max{method="close",namespace="myns",} 0.002
jedis_summary_seconds_max{method="ping",namespace="myns",} 0.0
jedis_summary_seconds_max{method="getDB",namespace="myns",} 0.002
jedis_summary_seconds_max{method="connect",namespace="myns",} 0.142
jedis_summary_seconds_max{method="isConnected",namespace="myns",} 0.001
jedis_summary_seconds_max{method="setDataSource",namespace="myns",} 0.013
jedis_summary_seconds_max{method="checkIsInMultiOrPipeline",namespace="myns",} 0.001
jedis_summary_seconds_max{method="set",namespace="myns",} 0.208
jedis_summary_seconds_max{method="getClient",namespace="myns",} 0.001
jedis_summary_seconds_max{method="resetState",namespace="myns",} 0.001
# HELP jedis_concurrent_gauge  
# TYPE jedis_concurrent_gauge gauge
jedis_concurrent_gauge{method="close",namespace="myns",} 0.0
jedis_concurrent_gauge{method="ping",namespace="myns",} 0.0
jedis_concurrent_gauge{method="getDB",namespace="myns",} 0.0
jedis_concurrent_gauge{method="connect",namespace="myns",} 0.0
jedis_concurrent_gauge{method="isConnected",namespace="myns",} 0.0
jedis_concurrent_gauge{method="setDataSource",namespace="myns",} 0.0
jedis_concurrent_gauge{method="checkIsInMultiOrPipeline",namespace="myns",} 0.0
jedis_concurrent_gauge{method="set",namespace="myns",} 0.0
jedis_concurrent_gauge{method="getClient",namespace="myns",} 0.0
jedis_concurrent_gauge{method="resetState",namespace="myns",} 0.0
```
### mybatis integration
sample项目请参考: [mybatis-sample-springboot](../../pepper-metrics-samples/mybatis-sample-springboot)  
pom中增加依赖：
```xml
<dependencies>
    <!-- pepper metrics dependencies -->
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-mybatis</artifactId>
        <version>1.0.11</version>
    </dependency>
    <!-- pepper-metrics datasource use prometheus by default -->
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.11</version>
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
18:27:28 [perf-mybatis:20190822182728] ---------------------------------------------------------------------------------------------------------------------------------------------------------
18:27:28 [perf-mybatis:20190822182728] | Metrics                                                                     Concurrent Count(Err/Sum)   P90(ms)   P99(ms)  P999(ms)   Max(ms)     Qps | 
18:27:28 [perf-mybatis:20190822182728] | com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId                  0         0/1950       0.6       1.4       2.5       3.5    32.5 | 
18:27:28 [perf-mybatis:20190822182728] | sample.mybatis.mapper.CityMapper.selectCityById                                      0         0/1950       0.8       2.4      56.6      56.6    32.5 | 
18:27:28 [perf-mybatis:20190822182728] ---------------------------------------------------------------------------------------------------------------------------------------------------------
```
prometheus指标输出情况：与其他相似，只是指标名区别

### Dubbo integration
sample项目请参考：[dubbo-sample-spring](../../pepper-metrics-samples/dubbo-sample-spring)

pom中添加依赖即可:

```xml
<dependencies>
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-dubbo</artifactId>
        <version>1.0.11</version>
    </dependency>
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.11</version>
    </dependency>
</dependencies>
```

### http integration
sample项目请参考：[servlet-sample-springboot](../../pepper-metrics-samples/servlet-sample-springboot)

pom中添加依赖：
```xml
<dependencies>
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-servlet</artifactId>
        <version>1.0.11</version>
    </dependency>
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.11</version>
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
* 如果不是使用spring-boot，以小于servlet3.0版本为例，web.xml中增加Filter配置：
```xml
<filter>
    <filter-name>ProfilerHttpFilter</filter-name>
    <filter-class>com.pepper.metrics.integration.servlet.PerfFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>ProfilerHttpFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

### motan integration
sample项目请参考：[motan-sample-jvm](../../pepper-metrics-samples/motan-sample-jvm)，[motan-sample-springboot](https://github.com/Lord-X/pepper-metrics/tree/master/pepper-metrics-samples/motan-sample-springboot)

在pom中添加依赖：

```xml
<dependencies>
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-motan</artifactId>
        <version>1.0.11</version>
    </dependency>
    <dependency>
        <groupId>top.zrbcool</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.11</version>
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


### druid integration

sample项目请参考：[druid-sample-spring](../../pepper-metrics-samples/druid-sample-spring)

在pom中添加依赖：

```xml
<dependency>
    <groupId>top.zrbcool</groupId>
    <artifactId>pepper-metrics-druid</artifactId>
    <version>1.0.12-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>top.zrbcool</groupId>
    <artifactId>pepper-metrics-ds-prometheus</artifactId>
    <version>1.0.12-SNAPSHOT</version>
</dependency>
```

为Druid数据源添加属性，以Spring xml配置形式为例：

```xml
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close" primary="true">
    <!-- ... -->
    <property name="filters" value="stat"/>
</bean>
```

然后用任何形式调用以下方法，将上面创建好的Druid数据源bean实例传给PepperMetrics，即可开启对指定数据源的健康信息采集：

```java
// 向DruidHealthTracker中添加数据源，即可收集健康信息
// 第一个参数：namespace：定义当前数据源名称，必须唯一且非空。例子中是广告业务的数据源，以"ad"命名
// 第二个参数：DruidDataSource：DruidDataSource数据源实例，将对这个实例进行健康数据采集，多个数据源add多次即可。
DruidHealthTracker.addDataSource("ad", dataSource);
```


### core use case
一般应用都是直接使用各种开源组件的集成，如果有特殊需要，例如需要有自定义的性能收集或者开发扩展插件时才需要了解core的使用，这里简单介绍，详细了解，请查看各个插件的使用方式，参考链接：[pepper-metrics-integration](../../pepper-metrics-integration)
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
同时配置SPI使其能被ExtensionLoader发现并加载，完整代码请参考sample项目：[core-sample-jvm](../../pepper-metrics-samples/core-sample-jvm)




