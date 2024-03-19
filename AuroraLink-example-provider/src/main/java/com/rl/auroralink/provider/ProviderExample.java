package com.rl.auroralink.provider;

import com.rl.auroralink.RpcApplication;
import com.rl.auroralink.common.service.UserService;
import com.rl.auroralink.config.RegistryConfig;
import com.rl.auroralink.config.RpcConfig;
import com.rl.auroralink.constant.RpcConstants;
import com.rl.auroralink.model.ServiceMetaInfo;
import com.rl.auroralink.registry.LocalRegistry;
import com.rl.auroralink.registry.Registry;
import com.rl.auroralink.registry.RegistryFactory;
import com.rl.auroralink.server.HttpServer;
import com.rl.auroralink.server.VertxHttpServer;
import com.rl.auroralink.utils.ConfigUtils;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 简易服务提供者
 * @date 2024/3/17 15:00:13
 */
public class ProviderExample {
    public static void main(String[] args) throws Exception {
        //RPC框架初始化
        RpcApplication.init();

        //注册服务
        String serviceName=UserService.class.getName();
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);

        //将服务注册到服务中心
        RpcConfig rpcConfig=RpcApplication.getRpcConfig();
        RegistryConfig registryConfig=rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getPort());

        registry.registry(serviceMetaInfo);

        //启功web服务
        HttpServer httpServer=new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getPort());
    }
}
