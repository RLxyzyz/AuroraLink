package com.rl.auroralink.config;

import lombok.Data;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description RPC框架注册中心配置
 * @date 2024/3/18 11:14:01
 */
@Data
public class RegistryConfig {


    /**
     * 注册中心类别
     * */
    private String registry="etcd";


    /**
     * 注册中心地址
     * */
    private String address="http://localhost:2380";


    /*
    * 用户名
    * */
    private String username;



    /**
     * 密码
     * */
    private String password;


    /**
     * 超时时间
     * */
    private Long timeout=10000L;

}
