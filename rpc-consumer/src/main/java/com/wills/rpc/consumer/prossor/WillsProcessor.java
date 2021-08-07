package com.wills.rpc.consumer.prossor;

import com.wills.rpc.consumer.annotation.RpcReturn;
import com.wills.rpc.consumer.proxy.RpcProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @ClassName WillsProcessor
 * @Date 2021/8/7 17:57
 * @Author 王帅
 * @Version 1.0
 * @Description
 * bean 的后置增强
 */
@Component
public class WillsProcessor implements BeanPostProcessor {

    @Autowired
    private RpcProxy proxy;

    /**
     * 自定义注解的注入
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 查看 bean的字段中 有没有对应的注解
        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getAnnotation(RpcReturn.class) != null) {
                // 获取代理对象
                Object target = this.proxy.getProxy(field.getType());
                try {
                    // 设置相关属性 属性注入
                    field.setAccessible(true);
                    field.set(bean, target);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
