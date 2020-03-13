### Notes
* 必须手动添加motan的filter，pepper才能生效
```java
ProtocolConfigBean config = new ProtocolConfigBean();
config.setDefault(true);
config.setName("motan");
config.setMaxContentLength(1048576);
// 配置pepper扩展motan的filter
config.setFilter("pepperProfiler");
```