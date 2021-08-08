package com.wills.rpc.provider.netty.handler;

import com.alibaba.fastjson.JSON;
import com.wills.entity.Request;
import com.wills.entity.Response;
import com.wills.entity.enum_code.HttpCode;
import com.wills.rpc.provider.annotation.RPC;
import com.wills.rpc.provider.entity.Singleton;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ServerHandler
 * @Date 2021/8/7 11:18
 * @Author 王帅
 * @Version 1.0
 * @Description
 * // 因为设置了 @Component 所以在spring容器中是单例的，所以需要进行通道共享要使用 @ChannelHandler.Sharable 注解
 * 本处理类的作用：
 * 1. 将标有 @Rpc 注解的 bean进行缓存
 * 2. 接收客户端的请求
 * 3. 根据传递过来的beanName从缓存中查找
 * 4. 通过反射调用bean的方法
 * 5. 给客户端响应
 */
@Component
@ChannelHandler.Sharable // 设置通道共享
public class ServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {



    /**
     * 将标有 @Rpc 注解的 bean进行缓存
     * @param context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, Object> map = context.getBeansWithAnnotation(RPC.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // 拿到具体的bean
            Object bean = entry.getValue();
            if(bean.getClass().getInterfaces().length == 0 ){
                throw new RuntimeException("暴露的服务必须要实现接口！");
            }
            String beanName = bean.getClass().getInterfaces()[0].getName();
            System.out.println("抓取到包含 @RPC 的类" + beanName);
            Singleton.getBeanInstance().put(beanName, bean);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println("收到["+ ctx.channel().remoteAddress() +"]消息：\r\n" + s);

        Request request = JSON.parseObject(s, Request.class);

        Response res = new Response();

        try{
            res.setData(handler(request));
            res.setCode(HttpCode.OK.getCode());
            res.setMsg(HttpCode.OK.getMsg());
        }catch (Exception e){
            e.printStackTrace();
            res.setMsg(e.getMessage());
            res.setCode(HttpCode.ERROR.getCode());
        }

        ctx.writeAndFlush(JSON.toJSONString(res));

    }

    public Object handler(Request request){
        Map<String, Object> instance = Singleton.getBeanInstance();
        Object bean = instance.get(request.getClassName());
        if(bean == null){
            throw new RuntimeException("服务端找不到这个bean");
        }

        try {
            // 通过反射调用bean方法
            Class<?> clazz = bean.getClass();
            Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
            // 调用方法，返回方法调用后的值
            Object[] parameters = request.getParameters();
            // BUG发现并解决！ 这里出错了！ 导致JSON无法转化为目标类，解决方法就是来一次强转即可
            if(method.getParameterTypes()!= null && method.getParameterTypes().length != 0){
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = JSON.parseObject(JSON.toJSONString(parameters[i]),parameterTypes[i]);
                }
            }
            return method.invoke(bean,parameters);
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
