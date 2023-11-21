package com.lc.rpc.harbor.processor;

import com.alibaba.fastjson.JSON;
import com.lc.rpc.harbor.annotation.OrpcService;
import com.lc.rpc.proxy.ServerProxyFactory;
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
        System.out.println("ServiceApplicationListener：执行 setApplicationContext...");
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(OrpcService.class);
            System.out.println("ServiceApplicationListener：" + JSON.toJSONString(beansWithAnnotation));
            ServerProxyFactory.build(beansWithAnnotation);
        }
    }

}
