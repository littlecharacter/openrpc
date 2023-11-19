package com.lc.rpc.demo.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author gujixian
 * @since 2023/11/20
 */
@Service
public final class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private SpringUtil(){}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }


}
