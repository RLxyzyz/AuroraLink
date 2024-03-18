package com.rl.auroralink.registry;

import com.rl.auroralink.config.RegistryConfig;
import com.rl.auroralink.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 注册接口
 * @date 2024/3/18 11:26:17
 */
public interface Registry {
    /**
     * 初始化
     * */
    void init(RegistryConfig registryConfig);


    /**
     * 注册服务
     * */

    void registry(ServiceMetaInfo serviceMetaInfo)throws Exception;



    /**
     * 注销服务
     *
     * */
    void unRegister(ServiceMetaInfo serviceMetaInfo);



    /**
     * 服务发现
     * */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);




    /**
     * 服务销毁
     * */
    void destroy();

}
