package com.rl.auroralink.serializer;

import com.rl.auroralink.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 序列化器工厂
 * @date 2024/3/17 18:53:26
 */
public class SerializerFactory {
    static {
        SpiLoader.load(Serializer.class);

    }
    private static final  Serializer DEFAULT_SERIALIZER=new JdkSerializer();

    public static Serializer getInstance(String key)
    {
        Serializer instance = SpiLoader.getInstance(Serializer.class, key);
        System.out.println(instance.getClass().getName());
        return instance;
    }

    public static Serializer getInstance(){
        return DEFAULT_SERIALIZER;
    }
}
