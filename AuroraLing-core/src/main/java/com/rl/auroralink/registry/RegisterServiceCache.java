package com.rl.auroralink.registry;

import com.rl.auroralink.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 注册中心服务本地缓存
 * @date 2024/3/18 20:45:12
 */
public class RegisterServiceCache {
    /**
     * 服务缓存
     * */
    List<ServiceMetaInfo> serviceCache;


    /**
     * 写缓存
     * */
    void writeCache(List<ServiceMetaInfo> newServiceCache)
    {
        this.serviceCache=serviceCache;
    }


    /**
     * 读缓存
     * */
    List<ServiceMetaInfo> readCache()
    {
        return this.serviceCache;
    }


    /**
     * 清空缓存
     * */
    void clearCache()
    {
        this.serviceCache=null;
    }
}
