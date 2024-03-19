package com.rl.auroralink.config;

import com.rl.auroralink.serializer.Serializer;
import com.rl.auroralink.serializer.SerializerKeys;
import lombok.Data;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description RPC框架配置
 * @date 2024/3/17 13:33:47
 */
@Data
public class RpcConfig {
    /**
     *名称
     * */
    private String name="AuroraLink";

    /**
     * 版本号
     * */
    private String version="1.0";
    /**
     *
     * 服务器主机名
     * */
    private String serverHost="localhost";


    /**
     * 服务器端口号
     * */
    private Integer port=8112;


    /**
     * mock模拟调用
     * */
    private boolean mock=false;


    /**
     * 序列化器
     * */
    private String serializer= SerializerKeys.JDK;


    /**
     * 注册中心配置
     * */
    private RegistryConfig registryConfig=new RegistryConfig();
}
