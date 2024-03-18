package com.rl.auroralink.registry;

import com.rl.auroralink.spi.SpiLoader;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 服务注册工厂
 * @date 2024/3/18 12:59:16
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册工厂
     * */
    private static final Registry DEFAULT_REGISTRY=new EtcdRegistry();


    /**
     * 获取实例
     * */
    public static Registry getInstance(String key)
    {
        return SpiLoader.getInstance(Registry.class,key);
    }
}
