package com.rl.auroralink.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 配置工具类
 * @date 2024/3/17 13:37:01
 */
public class ConfigUtils {
    /**
     * 加载配置对象
     * */
    public static <T> T loadConfig(Class<T> tClass,String prefix)
    {
        return loadConfig(tClass,prefix,"");
    }

    public static  <T> T loadConfig(Class<T> tClass,String prefix,String environment){
        StringBuilder configFileBuilder=new StringBuilder("application");
        if(StrUtil.isNotBlank(environment))
        {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        Props props=new Props(configFileBuilder.toString());
        return props.toBean(tClass,prefix);
    }
}
