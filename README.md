# Pepper Metrics Project  
![GitHub stars](https://img.shields.io/github/stars/zrbcool/pepper-metrics.svg?style=social)
![GitHub followers](https://img.shields.io/github/followers/zrbcool.svg?style=social)

## Overview
Pepper Metrics基于[RED](https://grafana.com/blog/2018/08/02/the-red-method-how-to-instrument-your-services/)理论，即对每个服务
（这里的服务特指进程中的某种调用，比如调用一次数据库查询）进行RED指标收集，并持久化到数据库，并通过dashboard进行展示，辅助进行性能趋势分析。

## Support Us
如果项目对你有帮助，请狠击**右上角**那个叫**Star**的按钮，如下图：  
![](http://oss.zrbcool.top/picgo/give-us-star.png)  
  
或者觉得我们有很多不足，请狠击**Issues**然后留下你的问题，我们会第一时间回复
## Document / WIKI
https://github.com/zrbcool/pepper-metrics/wiki
## Features
- 对Jedis/JedisCluster操作进行性能收集分析
- 对Http请求进行性能分析
- 对Mybatis的数据库操作进行性能分析
- 对Motan/Dubbo的RPC调用进行性能分析（支持调用方及服务方）
- 对多种时序数据库数据源的支持，目前默认为Prometheus
- 开箱即用的Grafana Dashboard，方便对历史趋势进行分析
- 基于SPI的可扩展架构，非常容易开发插件支持更多的开源组件

## Who use
酷划在线成立于2014年，是国内激励广告行业的领军者。酷划致力于打造一个用户、广告主、平台三方共赢的激励广告生态体系，旗下产品“酷划锁屏”“淘新闻”分别为锁屏、资讯行业的领跑者。  
[![](docs/logos/coohua-logo.png)](https://www.coohua.com/) [![](docs/logos/taonews-logo.png)](https://www.coohua.com/)

## Quickly Run A Demo Within 10 minutes
请参考独立项目：[https://github.com/zrbcool/pepper-metrics-demo](https://github.com/zrbcool/pepper-metrics-demo)    
线上Demo：[http://blog.zrbcool.top/](http://blog.zrbcool.top/)  


## Quick Start  
以Mybatis集成为例，更多其他请参考：[User Guide](https://github.com/zrbcool/pepper-metrics/wiki/ZH-User-Guide#samples)  
- 增加maven依赖
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
- 配置Pepper Metrics定制的Mybatis插件使集成生效
```xml
<configuration>
    <typeAliases>
        ...
    </typeAliases>
    <!-- 加入如下配置 -->
    <plugins>
        <plugin interceptor="com.pepper.metrics.integration.mybatis.MybatisProfilerPlugin" />
    </plugins>
    <mappers>
        ...
    </mappers>
</configuration>
```
- 日志输出效果：
```bash
18:27:28 [perf-mybatis:20190822182728] ---------------------------------------------------------------------------------------------------------------------------------------------------------
18:27:28 [perf-mybatis:20190822182728] | Metrics                                                                     Concurrent Count(Err/Sum)   P90(ms)   P99(ms)  P999(ms)   Max(ms)     Qps | 
18:27:28 [perf-mybatis:20190822182728] | com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId                  0         0/1950       0.6       1.4       2.5       3.5    32.5 | 
18:27:28 [perf-mybatis:20190822182728] | sample.mybatis.mapper.CityMapper.selectCityById                                      0         0/1950       0.8       2.4      56.6      56.6    32.5 | 
18:27:28 [perf-mybatis:20190822182728] ---------------------------------------------------------------------------------------------------------------------------------------------------------
```
- Prometheus指标输出效果（默认的实现，可以修改为其他数据库）
```bash
 ✗ curl localhost:9146/metrics
# HELP app_mapper_summary_seconds_max  
# TYPE app_mapper_summary_seconds_max gauge
app_mapper_summary_seconds_max{class="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml",operation="sample.mybatis.mapper.CityMapper.selectCityById",} 0.051129036
app_mapper_summary_seconds_max{class="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml",operation="com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId",} 0.011559611
# HELP app_mapper_summary_seconds  
# TYPE app_mapper_summary_seconds summary
app_mapper_summary_seconds{class="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml",operation="sample.mybatis.mapper.CityMapper.selectCityById",quantile="0.9",} 5.5296E-4
app_mapper_summary_seconds{class="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml",operation="sample.mybatis.mapper.CityMapper.selectCityById",quantile="0.99",} 0.001765376
app_mapper_summary_seconds{class="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml",operation="sample.mybatis.mapper.CityMapper.selectCityById",quantile="0.999",} 0.052424704
app_mapper_summary_seconds{class="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml",operation="sample.mybatis.mapper.CityMapper.selectCityById",quantile="0.99999",} 0.052424704
app_mapper_summary_seconds_count{class="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml",operation="sample.mybatis.mapper.CityMapper.selectCityById",} 3040.0
app_mapper_summary_seconds_sum{class="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml",operation="sample.mybatis.mapper.CityMapper.selectCityById",} 1.45711331
app_mapper_summary_seconds{class="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml",operation="com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId",quantile="0.9",} 4.4032E-4
app_mapper_summary_seconds{class="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml",operation="com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId",quantile="0.99",} 0.001308672
app_mapper_summary_seconds{class="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml",operation="com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId",quantile="0.999",} 0.002881536
app_mapper_summary_seconds{class="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml",operation="com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId",quantile="0.99999",} 0.012056576
app_mapper_summary_seconds_count{class="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml",operation="com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId",} 3040.0
app_mapper_summary_seconds_sum{class="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml",operation="com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId",} 0.772147736
# HELP app_mapper_concurrent_gauge  
# TYPE app_mapper_concurrent_gauge gauge
app_mapper_concurrent_gauge{class="com/pepper/metrics/sample/mybatis/mapper/CityMapper.xml",operation="sample.mybatis.mapper.CityMapper.selectCityById",} 0.0
app_mapper_concurrent_gauge{class="com/pepper/metrics/sample/mybatis/mapper/HotelMapper.xml",operation="com.pepper.metrics.sample.mybatis.mapper.HotelMapper.selectByCityId",} 0.0

```
### Maven dependency
以Mybatis为例，更多其他请参考：[User Guide](https://github.com/zrbcool/pepper-metrics/wiki/ZH-User-Guide#samples)  
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

## Building  
```bash
mvn clean package install -DskipTests
```

## Contact  
* Bugs: [Issues](https://github.com/zrbcool/pepper-metrics/issues/new?template=dubbo-issue-report-template.md)
* Plan & Progress: [Trello](https://trello.com/b/WfTQtssJ/pepper-metrics)
* Dingtalk chat group [Link](https://qr.dingtalk.com/action/joingroup?code=v1,k1,U4KKXEbTFBpuMbQMIQNij2IYszit+yktsAJh/9NjLFM=&_dt_no_comment=1&origin=11):  
![](http://oss.zrbcool.top/picgo/pepper-metrics-dingtalk-qrcode.png) 
* Developer
    * [@zrbcool](https://github.com/zrbcool)
    * [@Lord-X](https://github.com/Lord-X)

## Special Thanks
感谢作者所在公司[酷划在线](https://www.coohua.com/)给作者提供开放的技术环境，并充分支持回馈开源社区项目。

# License
Pepper Metrics is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
