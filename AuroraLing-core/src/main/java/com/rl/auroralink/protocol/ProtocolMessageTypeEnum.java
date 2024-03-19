package com.rl.auroralink.protocol;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 协议消息的类型枚举
 * @date 2024/3/19 11:28:41
 */
public enum ProtocolMessageTypeEnum {
    REQUESR(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);


    private final int key;


    ProtocolMessageTypeEnum(int key)
    {
        this.key=key;
    }


    /**
     * 根据key获取枚举
     * */
    public static ProtocolMessageTypeEnum getEnumByKey(int key)
    {
        for (ProtocolMessageTypeEnum value : ProtocolMessageTypeEnum.values()) {
            if(value.key==key)
            {
                return value;
            }
        }
        return null;
    }

}
