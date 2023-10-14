package com.lc.rpc.harbor.annotation;

import com.lc.rpc.harbor.processor.ConfigurationLoader;
import com.lc.rpc.harbor.processor.ReferenceInjectBeanPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author gujixian
 * @since 2023/10/14
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        ConfigurationLoader.class,
        ReferenceInjectBeanPostProcessor.class,
})
public @interface EnableOrpcConsumer {
    String path() default "";
}
