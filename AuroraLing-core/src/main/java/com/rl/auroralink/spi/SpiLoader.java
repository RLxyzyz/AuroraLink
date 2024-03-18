package com.rl.auroralink.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.rl.auroralink.serializer.Serializer;
import io.netty.util.internal.ResourcesUtil;
import lombok.extern.slf4j.Slf4j;

import javax.management.InstanceNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description SPI加载器（支持键值对映射）
 * @date 2024/3/17 20:08:34
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已经加载的类：接口名=>(key=>实现类）
     *
     * */
    private static Map<String, Map<String,Class<?>>> loaderMap=new ConcurrentHashMap<>();


    /**
     * 对象实例缓存（避免重复new,类路径=>对象实例，单例模式
     * */
    private static Map<String,Object> instanceCache=new ConcurrentHashMap<>();


    /**
     *
     * 系统SPI目录
     * */
    private static final String RPC_SYSTEM_SPI_PATH="META-INF/rpc/system/";


    /**
     * 用户自定义实现SPI目录
     * */
    private static final String RPC_CUSTOM_SPI_PATH="META_INF/rpc/custom/";



    /**
     * 扫描路径
     * */
    private static final String[] SCAN_PATHS=new String[]{
            RPC_CUSTOM_SPI_PATH,RPC_SYSTEM_SPI_PATH
    };


    /**
     * 动态加载的类列表
     * */
    private static final List<Class<?>> LOAD_CLASS_LIST= Arrays.asList(Serializer.class);


    /**
     * 加载所有的类型
     * */
    public static void loadAll()
    {
        log.info("load all SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }


    /**
     * 获取某个接口的实例
     * */
    public static <T> T getInstance(Class<?> tClass,String key)
    {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap==null)
        {
            throw new RuntimeException(String.format("SPILoader 未加载 %s 类型",tClassName));
        }
        if (!keyClassMap.containsKey(key))
        {
            throw new RuntimeException(String.format("SPILoader 的%s 不存在 key=%s的类型",tClassName,key));
        }
        //获取到需要加载的指定的实现类的实例
        Class<?> implClass=keyClassMap.get(key);
        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName))
        {
            try {
                instanceCache.put(implClassName,implClass.newInstance());
            }catch (IllegalAccessException | InstantiationException e)
            {
                String errorMessage=String.format("%s类实例化失败",implClassName);
                throw new RuntimeException(errorMessage,e);
            }

        }
        return (T)instanceCache.get(implClassName);
    }


    /**
     * 加载某个类型
     * */
    public static Map<String,Class<?>> load(Class<?> loadClass) {
        log.info("load type: {}",loadClass.getName());

         //扫描目录用户自定义的SPI的优先级高于系统的SPI
      Map<String,Class<?>> keyClassMap=new HashMap<>();
        for (String scanPath : SCAN_PATHS) {
            List<URL> resources= ResourceUtil.getResources(scanPath+loadClass.getName());
            for (URL resource : resources) {
                try{
                    InputStreamReader inputStreamReader=new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                    String line;
                    while ((line=bufferedReader.readLine())!=null)
                    {
                        String[] strArray = line.split("=");
                        String key=strArray[0];
                        String className=strArray[1];
                        keyClassMap.put(key,Class.forName(className));

                    }
                }catch (Exception e)
                {
                    log.error("spi resource load error: {}",e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;

    }
}
