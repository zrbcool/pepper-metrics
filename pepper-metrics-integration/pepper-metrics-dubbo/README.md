# Pepper-Metrics-Dubbo扩展机制原理

## 1 开发此扩展点的目的

Pepper-Metrics需要收集Dubbo在Provider端和Consumer端的接口响应性能数据，以便存储到DataSource中或提供给Printer使用。

在此背景下，我们需要对Provider端和Consumer端的每一次请求和响应进行监控。在Dubbo中，可以通过扩展 `org.apache.dubbo.rpc.Filter` 接口实现。

## 2 org.apache.dubbo.rpc.Filter介绍

Filter可以理解为调用过程拦截，每次方法调用该拦截器都会生效，扩展时需要注意对性能的影响。

用户定义的Filter默认在已有的Filter之后被执行。

## 3 Pepper-Metrics-Dubbo的Filter实现

在 `Pepper-Metrics-Dubbo` 中，`DubboProfilerFilterTemplate` 类实现了 `org.apache.dubbo.rpc.Filter` 接口。

这是一个模板类，定义了 `Filter.invoke()` 方法的通用实现，由于具体收集profile时，针对 `Provider` 和 `Consumer` 需要不同的收集器，这里通过其子类 `DubboProviderProfilerFilter` 和 `DubboConsumerProfilerFilter` 分别实现。

上述的类关系可通过下图描述：

![Pepper-Metrics-Dubbo类图](http://image.feathers.top/image/Pepper-Metrics-Dubbo类图.png)

写完实现类后，需要在项目的 `resources` 目录下配置Dubbo的扩展文件。

在 `resources` 下创建 `META-INF/dubbo/org.apache.dubbo.rpc.Filter` 文件，内容如下：

```text
dubboProviderProfiler=com.pepper.metrics.integration.dubbo.DubboProviderProfilerFilter
dubboConsumerProfiler=com.pepper.metrics.integration.dubbo.DubboConsumerProfilerFilter
```

这样Dubbo就可以扫描到自定义的扩展点。

接下来需要将自定义的扩展点配置到Dubbo中，告诉Dubbo我要使用这个Filter，分别在 `Provider` 和 `Consumer` 中配置：

首先看一下 `Provider`：
```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xmlns="http://www.springframework.org/schema/beans" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <context:property-placeholder/>

    <dubbo:application name="demo-provider"/>

    <dubbo:registry address="multicast://224.5.6.7:1234"/>

    <bean id="demoService" class="com.pepper.metrics.sample.dubbo.spring.provider.DemoServiceImpl"/>

    <!-- 在这里配置自定义的扩展点 -->
    <dubbo:service filter="default,dubboProviderProfiler" interface="com.pepper.metrics.sample.dubbo.spring.api.DemoService" ref="demoService" />

</beans>
```

> 说明：`default` 代表已有的扩展点，`dubboProviderProfiler` 是我们自定义的扩展点，这样配置表示我们自定义的扩展点在已有的扩展点之后执行。

同样，在 `Consumer` 端配置自定义扩展点：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xmlns="http://www.springframework.org/schema/beans" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <context:property-placeholder/>

    <dubbo:application name="demo-consumer"/>

    <dubbo:registry address="multicast://224.5.6.7:1234"/>

    <!-- 在这里配置自定义的扩展点 -->
    <dubbo:reference filter="default,dubboConsumerProfiler" id="demoService" check="true" interface="com.pepper.metrics.sample.dubbo.spring.api.DemoService" />

</beans>
```

## 4 自定义扩展点的自动激活

从上文得知，我们自定义的扩展点必须要修改配置才能生效，这样一来，是有代码侵入的。那么，能不能引入 `pepper-metrics-dubbo` 的jar包后，不用修改配置，就直接生效呢？

**答案是：当然可以！**

`pepper-metrics-dubbo` 使用了Dubbo提供的 `@Activate` 机制。这个注解可用于类或方法上。其作用是可以让Dubbo自动激活此扩展，从而简化配置。

以 `Provider` 为案例，看一下这个东西在 `pepper-metrics-dubbo` 里是咋用的。

```java
@Activate(group = {PROVIDER}) // is here
public class DubboProviderProfilerFilter extends DubboProfilerFilterTemplate {
    @Override
    void afterInvoke(String[] tags, long begin, boolean isError) {
        // ...
    }

    @Override
    void beforeInvoke(String[] tags) {
        // ...
    }
}
```

首先，如果只配置 `@Activate` 注解，不自定义其属性的话，会无条件自动激活所有扩展点。在我们的项目中，就是会同时激活 `DubboConsumerProfilerFilter` 和 `DubboProviderProfilerFilter`。

但在我们的需求中是不能同时激活两个扩展点的。如果同时激活，服务提供方和调用方都会同时调用两个扩展点。而我们需要的是提供方调用 `Provider`，调用方调用 `Consumer`。

这可以通过 `group` 来实现。定义了 `group` 之后，就只对特定的group激活了。

在Filter中，有两个group：

```java
String PROVIDER = "provider";
String CONSUMER = "consumer";
```

定义为 `PROVIDER` 就只对提供方生效，定义为 `CONSUMER` 就只对调用方生效，也可以同时定义，那就同时生效。

这样一来，只需要依赖 `pepper-metrics-dubbo` 包即可激活扩展点了。

具体的sample可以参考 `pepper-metrics-samples/dubbo-sample-spring`。


## 参考

* [Dubbo: 调用拦截扩展](https://dubbo.apache.org/zh-cn/docs/dev/impls/filter.html)
* [Dubbo: 扩展点加载](https://dubbo.apache.org/zh-cn/docs/dev/SPI.html)