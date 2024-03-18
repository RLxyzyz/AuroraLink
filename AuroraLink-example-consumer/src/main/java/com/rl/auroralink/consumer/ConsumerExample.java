package com.rl.auroralink.consumer;

import com.rl.auroralink.common.model.User;
import com.rl.auroralink.common.service.UserService;
import com.rl.auroralink.config.RpcConfig;
import com.rl.auroralink.proxy.ServiceProxyFactory;
import com.rl.auroralink.utils.ConfigUtils;

import java.awt.desktop.UserSessionEvent;
import java.lang.reflect.Proxy;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 服务消费者测试
 * @date 2024/3/17 14:06:06
 */
public class ConsumerExample {
    public static void main(String[] args) {
        //获取代理
        UserService userService= ServiceProxyFactory.getProxy(UserService.class);

        User user=new User();
        user.setName("rl");
        User newUser = userService.getUser(user);
        if(newUser!=null)
        {
            System.out.println(newUser.getName());
        }else{
            System.out.println("user==null");
        }
        short number = userService.getNumber();
        System.out.println(number);

    }
}
