package com.rl.auroralink.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description Mock服务代理（JDK动态代理实现）
 * @date 2024/3/17 14:16:13
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke{}",method.getName());
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> type) {
        if (type==boolean.class)
        {
            return false;
        }else if(short.class==type)
        {
            return (short)1;
        }else if(type==int.class)
        {
            return 0;
        }else if(type==long.class)
        {
            return 0L;
        }else{
            return null;
        }
    }
}
