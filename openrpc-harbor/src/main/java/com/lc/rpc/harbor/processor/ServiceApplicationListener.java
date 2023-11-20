package com.lc.rpc.harbor.processor;

import com.alibaba.fastjson.JSON;
import com.lc.rpc.common.Configuration;
import com.lc.rpc.common.Constant;
import com.lc.rpc.harbor.annotation.OrpcService;
import com.lc.rpc.proxy.ServerProxyFactory;
import com.lc.rpc.register.ServiceRegister;
import com.lc.rpc.register.impl.ZkServiceRegister;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
            // 1，把这些 bean 生成代理：Map<服务接口的权限定名，Invoker代理>
            Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(OrpcService.class);
            System.out.println("ServiceApplicationListener：" + JSON.toJSONString(beansWithAnnotation));
            ServerProxyFactory.build(beansWithAnnotation);
            // 2，TODO 启动服务端

            // 3，服务注册
            InetAddress address;
            try {
                address = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            String serviceAddress = address.getHostAddress() + ":" + Configuration.getProperty(Constant.OPENRPC_SERVICE_PORT);
            ServiceRegister register = new ZkServiceRegister();
            beansWithAnnotation.forEach((beanName, bean) -> {
                System.out.println("beanName:" + beanName + ", bean" + bean);
                Class<?> clazz = bean.getClass();
                for (Class<?> face : clazz.getInterfaces()) {
                    System.out.println(face.getName());
                    register.registry(face.getName(), serviceAddress, "providers");
                }
            });
        }
    }

}
