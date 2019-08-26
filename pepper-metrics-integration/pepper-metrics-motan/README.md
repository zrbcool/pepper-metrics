# Pepper-Metrics-Motan扩展机制原理

## 1 开发此扩展点的目的

Pepper-Metrics需要收集Motan在Provider端和Consumer端的接口响应性能数据，以便存储到DataSource中或提供给Printer使用。

在此背景下，我们需要对Provider端和Consumer端的每一次请求和响应进行监控。在Motan中，可以通过扩展 `com.weibo.api.motan.filter.Filter` 接口实现。


## 未完待续...