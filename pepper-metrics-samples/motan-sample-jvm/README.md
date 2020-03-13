### Notes
* 必须手动添加motan的filter，pepper才能生效
```java
// 配置RPC协议
ProtocolConfig protocol = new ProtocolConfig();
protocol.setId("motan");
protocol.setName("motan");
// 配置pepper扩展motan的filter
protocol.setFilter("pepperProfiler");
```