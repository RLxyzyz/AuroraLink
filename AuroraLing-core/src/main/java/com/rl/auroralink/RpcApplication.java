package com.rl.auroralink;

import com.rl.auroralink.config.RegistryConfig;
import com.rl.auroralink.config.RpcConfig;
import com.rl.auroralink.constant.RpcConstants;
import com.rl.auroralink.registry.Registry;
import com.rl.auroralink.registry.RegistryFactory;
import com.rl.auroralink.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 存放全局的变量，双重检查锁实现单例模式
 * @date 2024/3/17 13:57:37
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化,支持自定义配置
     * */
    public static void init(RpcConfig newRpcConfig)
    {
        rpcConfig=newRpcConfig;
        log.info("rpc init,config={}",newRpcConfig.toString());
        RegistryConfig registryConfig=rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init,config={}",registryConfig);
    }

    /**
     * 初始化
     * */
    public static void init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig= ConfigUtils.loadConfig(RpcConfig.class, RpcConstants.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e)
        {
            log.info("config error",e);
            //配置加载失败，使用默认加载配置
            newRpcConfig=new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     * */
    public static RpcConfig getRpcConfig(){
        if (rpcConfig==null)
        {
            synchronized (RpcApplication.class){
                if (rpcConfig==null)
                {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
