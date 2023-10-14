package com.lc.rpc.harbor.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author gujixian
 * @since 2023/10/14
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface OrpcService {
}
