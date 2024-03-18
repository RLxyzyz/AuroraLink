package com.rl.auroralink.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rl.auroralink.model.RpcRequest;
import com.rl.auroralink.model.RpcResponse;

import java.io.IOException;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description JSON序列化器
 * @date 2024/3/17 15:25:53
 */
public class JsonSerializer implements Serializer{
    private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, type);
        if(obj instanceof RpcRequest)
        {
            return handleRequest((RpcRequest)obj,type);
        }
        if (obj instanceof RpcResponse)
        {
            return handleResponse((RpcResponse)obj,type);
        }
        return obj;
    }

    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type)throws IOException {
        Object data = rpcResponse.getData();
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(data);
        rpcResponse.setData(OBJECT_MAPPER.readValue(bytes,type));
        return type.cast(rpcResponse);

    }

    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();
        for(int i=0;i<parameterTypes.length;i++)
        {
            Class<?> clazz = parameterTypes[i];
            //如果类型不相同则重更新处理一下
            if (!clazz.isAssignableFrom(args[i].getClass()))
            {
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i]=OBJECT_MAPPER.readValue(bytes,clazz);
            }
        }
        return type.cast(rpcRequest);
    }
}
