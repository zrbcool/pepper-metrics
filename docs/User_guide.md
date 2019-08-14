# User Guide
## Samples
样例项目下载：
> ```bash
> git clone https://github.com/zrbcool/pepper-metrics.git
> cd pepper-metrics/pepper-metrics-samples
> ```
各种开源组件集成索引：
- [Jedis](User_guide.md#jedis-integration)
- [Mybatis](User_guide.md#mybatis-integration)
### jedis integration
pom中添加如下依赖
```xml
<dependencies>
    <!-- pepper-metrics-jedis dependency -->
    <dependency>
        <groupId>com.pepper</groupId>
        <artifactId>pepper-metrics-jedis</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <!-- pepper-metrics datasource use prometheus by default -->
    <dependency>
        <groupId>com.pepper</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
与Jedis集成（单机），具体参考[JedisSampleMain.java](../pepper-metrics-samples/jedis-sample-jvm/src/main/java/com/pepper/metrics/sample/jedis/JedisSampleMain.java)
```java
JedisPoolConfig config = new JedisPoolConfig();
config.setMaxTotal(300);
...//省略jedisPoolConfig的设置代码
config.setTestOnCreate(false);

// 与正常使用Jedis没有差异
// 只是这块创建JedisPool的时候换成PjedisPool实现即可
// 最后一个参数用于当应用连接多组Redis时在日志打印及指标展示时区分
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
```

与JedisCluster集成（集群），具体参考[JedisClusterSampleMain.java](../pepper-metrics-samples/jedis-sample-jvm/src/main/java/com/pepper/metrics/sample/jediscluster/JedisClusterSampleMain.java)
```java
String address = "192.168.100.180:9700,192.168.100.180:9701,192.168.100.180:9702,192.168.100.180:9703,192.168.100.180:9704,192.168.100.180:9705";
JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
config.setMaxTotal(300);
...//省略jedisPoolConfig的设置代码
config.setTestOnCreate(false);

String[] commonClusterRedisArray = address.split(",");
Set<HostAndPort> jedisClusterNodes = new HashSet<>();
for (String clusterHostAndPort : commonClusterRedisArray) {
    String host = clusterHostAndPort.split(":")[0].trim();
    int port = Integer.parseInt(clusterHostAndPort.split(":")[1].trim());
    jedisClusterNodes.add(new HostAndPort(host, port));
}
// 与正常使用JedisCluster没有差异
// 只修改这一处即可，PjedisClusterFactory.newPjedisCluster(...)，PjedisCluster完全兼容JedisCluster的API
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
pom中增加依赖：
```xml
<dependencies>
    <!-- pepper metrics dependencies -->
    <dependency>
        <groupId>com.pepper</groupId>
        <artifactId>pepper-metrics-mybatis</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <!-- pepper-metrics datasource use prometheus by default -->
    <dependency>
        <groupId>com.pepper</groupId>
        <artifactId>pepper-metrics-ds-prometheus</artifactId>
        <version>1.0.0-SNAPSHOT</version>
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