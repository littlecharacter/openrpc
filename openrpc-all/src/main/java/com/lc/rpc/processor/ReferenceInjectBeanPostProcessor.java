package com.lc.rpc.processor;

import com.alibaba.fastjson.JSON;
import com.lc.rpc.annotation.OrpcReference;
import com.lc.rpc.proxy.ClientProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;

/**
 * @author gujixian
 * @since 2023/10/14
 */
public class ReferenceInjectBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("ReferenceInjectBeanPostProcessor：执行 setBeanFactory...");
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("ReferenceInjectBeanPostProcessor：执行 postProcessBeforeInitialization...");
        // 获取 bean 所有属性字段
        Field[] fields = bean.getClass().getDeclaredFields();
        System.out.println("ReferenceInjectBeanPostProcessor：" + JSON.toJSONString(fields));
        for (Field field : fields) {
            // 字段是否被 @OrpcReference 修饰
            if (field.isAnnotationPresent(OrpcReference.class)) {
                // 1，TODO 从注册中心拉取服务注册信息
                // 2，TODO 构建Cluster负载均衡 + Server创建、启动连接服务端
                // 3，创建远程接口代理类，并赋给 bean 的属性
                OrpcReference annotation = field.getAnnotation(OrpcReference.class);
                // 获取注解属性
                // String value = annotation.value();
                // 获取字段名和类型
                String name = field.getName();
                Class<?> type = field.getType();
                field.setAccessible(true);
                try {
                    Object obj;
                    if (beanFactory.containsBeanDefinition(name)) {
                        System.out.println("ReferenceInjectBeanPostProcessor：代理对象从Spring工厂获得");
                        obj = beanFactory.getBean(name);
                    } else {
                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(type);
                        BeanDefinition definition = builder.getBeanDefinition();
                        beanFactory.registerBeanDefinition(name, definition);
                        // obj = type.newInstance();
                        System.out.println("ReferenceInjectBeanPostProcessor：代理对象从代理工厂获得");
                        obj = ClientProxyFactory.create(type);
                        beanFactory.registerSingleton(name, obj);
                    }
                    field.set(bean, obj);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
