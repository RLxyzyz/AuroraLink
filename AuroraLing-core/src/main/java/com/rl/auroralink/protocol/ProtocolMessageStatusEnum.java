package com.rl.auroralink.protocol;

import lombok.Getter;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 结果状态码
 * @date 2024/3/19 11:23:40
 */
@Getter
public enum ProtocolMessageStatusEnum {


    OK("ok",20),
    BAD_REQUEST("badRequest",40),
    BAD_RESPONSE("badResponse",50);

    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text,int value)
    {
        this.text=text;
        this.value=value;
    }


    /**
     * 根据value 获取枚举值
     * */
    public static ProtocolMessageStatusEnum getEnumByValue(int value)
    {
        for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()) {
            if (anEnum.value==value)
            {
                return anEnum;
            }
        }
        return null;
    }
}
