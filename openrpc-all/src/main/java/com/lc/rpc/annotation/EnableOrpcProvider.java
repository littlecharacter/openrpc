package com.lc.rpc.annotation;

import com.lc.rpc.processor.ConfigurationLoader;
import com.lc.rpc.processor.ServiceApplicationListener;
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
        ServiceApplicationListener.class,
})
public @interface EnableOrpcProvider {
    String path() default "";
}
