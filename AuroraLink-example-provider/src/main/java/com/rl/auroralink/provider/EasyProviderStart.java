package com.rl.auroralink.provider;

import com.rl.auroralink.common.service.UserService;
import com.rl.auroralink.registry.LocalRegistry;
import com.rl.auroralink.server.HttpServer;
import com.rl.auroralink.server.VertxHttpServer;

/**
 * @author 任磊
 * @version 1.0
 * @project rl-rpc
 * @description 服务启动类
 * @date 2024/3/16 11:20:28
 */
public class EasyProviderStart {
    public static void main(String[] args) {


        //服务注册
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        System.out.println(LocalRegistry.get(UserService.class.getName()));
        //提供服务
        HttpServer server=new VertxHttpServer();
        server.doStart(8080);
    }
}
