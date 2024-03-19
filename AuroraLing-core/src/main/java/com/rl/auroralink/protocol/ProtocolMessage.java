package com.rl.auroralink.protocol;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description 消息协议结构
 * @date 2024/3/19 11:14:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 消息头
     * */
    private Header header;


    /**
     * 消息体
     * */
    private T body;



    /**
     * 协议消息头
     * */
    @Data
    public static class Header{
        /**
         * 魔数
         * */
        private byte magic;

        /**
         * 版本号
         * */
        private byte version;


        /**
         * 序列化器
         * */
        private byte serializer;


        /**
         * 消息类型
         * */
        private byte type;


        /**
         * 状态
         * */
        private byte status;

        /**
         * 请求ID*/
        private long requestId;


        /**
         * 消息体长度
         * */
        private int bodyLength;
    }
}
