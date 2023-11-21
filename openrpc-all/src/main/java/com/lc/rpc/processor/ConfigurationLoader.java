package com.lc.rpc.processor;

import com.lc.rpc.annotation.EnableOrpc;
import com.lc.rpc.annotation.EnableOrpcConsumer;
import com.lc.rpc.annotation.EnableOrpcProvider;
import com.lc.rpc.common.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @author gujixian
 * @since 2023/10/14
 */
public class ConfigurationLoader implements ApplicationContextAware, BeanFactoryPostProcessor {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Properties properties = new Properties();
        // 1, 从 Spring Environment 中获取配置
        loadPropertiesFromEnvironment(properties);
        // 2, 从 OpenRPC 指定的配置文件中获取配置 --> 如果配置重复，会覆盖 Spring Environment 中的配置
        loadPropertiesFromAnnotation(beanFactory, properties);
        // 3, 集中存储到 Configuration
        System.out.println("ConfigurationLoader：" + properties.getProperty("openrpc.service.name"));
        Configuration.setProperties(properties);
    }

    private void loadPropertiesFromEnvironment(Properties properties) {
        ConfigurableEnvironment environment = (ConfigurableEnvironment) applicationContext.getEnvironment();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
                for (String name : enumerablePropertySource.getPropertyNames()) {
                    properties.setProperty(name, String.valueOf(enumerablePropertySource.getProperty(name)));
                }
            }
        }
    }

    private void loadPropertiesFromAnnotation(ConfigurableListableBeanFactory beanFactory, Properties properties) {
        // 获取所有被 EnableOrpc 注解修饰的bean
        Set<String> orpcBeanNameSet = beanFactory.getBeansWithAnnotation(EnableOrpc.class).keySet();
        // 遍历bean并获取注解属性值
        for (String beanName : orpcBeanNameSet) {
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) beanFactory.getBeanDefinition(beanName);
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            String filePath = (String) Objects.requireNonNull(metadata.getAnnotationAttributes(EnableOrpc.class.getName())).get("path");
            filePath = org.springframework.util.StringUtils.isEmpty(filePath) ? "config/openrpc.properties" : filePath;
            loadProperties(properties, filePath);
        }

        // 获取所有被 orpcConsumer 注解修饰的bean
        Set<String> orpcConsumerBeanNameSet = beanFactory.getBeansWithAnnotation(EnableOrpcConsumer.class).keySet();
        // 遍历bean并获取注解属性值
        for (String beanName : orpcConsumerBeanNameSet) {
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) beanFactory.getBeanDefinition(beanName);
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            String filePath = (String) Objects.requireNonNull(metadata.getAnnotationAttributes(EnableOrpcConsumer.class.getName())).get("path");
            filePath = org.springframework.util.StringUtils.isEmpty(filePath) ? "config/openrpc.properties" : filePath;
            loadProperties(properties, filePath);
        }

        // 获取所有被 orpcProvider 注解修饰的bean
        Set<String> orpcProviderBeanNameSet = beanFactory.getBeansWithAnnotation(EnableOrpcProvider.class).keySet();
        // 遍历bean并获取注解属性值
        for (String beanName : orpcProviderBeanNameSet) {
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) beanFactory.getBeanDefinition(beanName);
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            String filePath = (String) Objects.requireNonNull(metadata.getAnnotationAttributes(EnableOrpcProvider.class.getName())).get("path");
            filePath = StringUtils.isEmpty(filePath) ? "config/openrpc.properties" : filePath;
            loadProperties(properties, filePath);
        }
    }

    private void loadProperties(Properties properties, String filePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (Objects.isNull(inputStream)) {
                return;
            }
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
