package com.lc.rpc.harbor.processor;

import com.alibaba.fastjson.JSON;
import com.lc.rpc.harbor.annotation.OrpcService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author gujixian
 * @since 2023/10/14
 */
public class ServiceApplicationListener implements ApplicationListener<ApplicationEvent>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            // 1，把这些 bean 生成代理：Map<服务接口的权限定名，Invoker代理>
            Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(OrpcService.class);
            System.out.println("ServiceApplicationListener：" + JSON.toJSONString(beansWithAnnotation));
            beansWithAnnotation.forEach((key, value) -> System.out.println("beanName:" + key + ", bean" + value));
            // 2，TODO 启动服务端
            // 3，TODO 服务注册
        }
    }

}
