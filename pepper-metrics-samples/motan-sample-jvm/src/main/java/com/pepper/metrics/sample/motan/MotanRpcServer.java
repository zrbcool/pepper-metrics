package com.pepper.metrics.sample.motan;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.util.MotanSwitcherUtil;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-14
 */
public class MotanRpcServer {
    public static void main(String[] args) throws InterruptedException {
        ServiceConfig<HelloService> motanDemoService = new ServiceConfig<HelloService>();

        // 设置接口及实现类
        motanDemoService.setInterface(HelloService.class);
        motanDemoService.setRef(new HelloServiceImpl());

        // 配置服务的group以及版本号
        motanDemoService.setGroup("motan-demo-rpc");
        motanDemoService.setVersion("1.0");

        // 配置注册中心直连调用
        RegistryConfig registry = new RegistryConfig();

        //use local registry
        registry.setRegProtocol("local");

        // use ZooKeeper registry
//        registry.setRegProtocol("zookeeper");
//        registry.setAddress("127.0.0.1:2181");

        // registry.setCheck("false"); //是否检查是否注册成功
        motanDemoService.setRegistry(registry);

        // 配置RPC协议
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setId("motan");
        protocol.setName("motan");
        motanDemoService.setProtocol(protocol);

        protocol.setFilter("pepperProfiler");

        motanDemoService.setExport("motan:8002");
        motanDemoService.export();

        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);

        System.out.println("server start...");
    }
}
