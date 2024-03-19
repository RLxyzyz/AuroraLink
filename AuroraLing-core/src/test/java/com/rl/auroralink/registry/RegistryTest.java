package com.rl.auroralink.registry;

import com.rl.auroralink.config.RegistryConfig;
import com.rl.auroralink.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 注册中心测试类
 * @date 2024/3/18 14:50:07
 */
public class RegistryTest {
    final Registry registry=new EtcdRegistry();

    @Before
    public void init()
    {
        RegistryConfig registryConfig=new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.registry(serviceMetaInfo);

    }

    @Test
    public void heartBeat() throws Exception {
        register();
        Thread.sleep(60*1000L);
    }

    @Test
    public void unRegistry()
    {
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery(){
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMetaInfoList);
    }
}
