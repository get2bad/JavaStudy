package com.wills.rpc.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.wills.entity.Request;
import com.wills.entity.Response;
import com.wills.entity.User;
import com.wills.rpc.consumer.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName RpcProxy
 * @Date 2021/8/7 17:15
 * @Author 王帅
 * @Version 1.0
 * @Description
 */
@Component
public class RpcProxy {

    @Autowired
    private NettyClient client;

    static Map<Class,Object> SERVICE_PROXY = new HashMap<>();

    // 获取代理对象
    public Object getProxy(Class serviceClass) {
        Object proxy = SERVICE_PROXY.get(serviceClass);
        if (proxy == null) {
            // 如果不存在，就使用jdk动态代理创建
            proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{serviceClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Request request = new Request();
                    request.setRequestId(UUID.randomUUID().toString().replace("-",""));
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameters(args);
                    request.setParameterTypes(method.getParameterTypes());

                    // 发送消息
                    Object send = client.send(JSON.toJSONString(request));
                    // 拿到返回的结果
                    Response response = JSON.parseObject(send.toString(), Response.class);
                    if(response != null && response.getCode() != 200){
                        // 出现错误
                        throw new RuntimeException(response.getMsg());
                    }
                    // 拿到返回的数据 进行返回
                    return JSON.parseObject(response.getData().toString(), method.getReturnType());
                }
            });
            SERVICE_PROXY.put(serviceClass, proxy);
        }
        return proxy;
    }
}
