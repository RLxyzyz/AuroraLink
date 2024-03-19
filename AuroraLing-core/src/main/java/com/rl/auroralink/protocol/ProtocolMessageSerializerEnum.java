package com.rl.auroralink.protocol;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 序列化器的枚举
 * @date 2024/3/19 11:37:06
 */
public enum ProtocolMessageSerializerEnum {
    JDK(0,"jdk"),
    HESSIAN(1,"hessian"),
    KRYO(2,"kryo"),
    JSON(3,"json");

    private final int key;
    private final String value;

    ProtocolMessageSerializerEnum(int key,String value)
    {
        this.key=key;
        this.value=value;
    }


    /**
     * 获取值列表
     * */
    public static List<String> getValues()
    {

        return Arrays.stream(ProtocolMessageSerializerEnum.values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**根据key 获取枚举
     *
     * */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key)
    {
        for (ProtocolMessageSerializerEnum value : ProtocolMessageSerializerEnum.values()) {
            if (value.key==key)
            {
                return value;
            }
        }
        return null;
    }


    /**
     * 根据value获取枚举
     * */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value)
    {
        if(ObjectUtil.isEmpty(value))
        {
            return null;
        }
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.value.equals(value))
            {
                return anEnum;
            }
        }
        return null;
    }
}
