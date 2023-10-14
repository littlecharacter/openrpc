package com.lc.rpc.harbor.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrpcReference {
    String value() default "";
}
