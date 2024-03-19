package com.rl.auroralink.protocol;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 协议常量
 * @date 2024/3/19 11:20:32
 */
public interface ProtocolConstant {
    /**
     * 消息头长度
     * */
    int MESSAGE_HEADER_LENGTH=17;



    /**
     * 协议魔数
     * */
    byte PROTOCOL_MAGIC=0x1;



    /**
     * 协议版本号
     * */
    byte PROTOCOL_VERSION = 0x1;
}
