package com.montos.boot.montos.mq.core.annotation;

import com.montos.boot.montos.mq.core.config.MontosMQCoreConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 定义是否开启JsonMQService功能
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MontosMQCoreConfiguration.class)
public @interface EnableJsonMQ {

    String[] value() default {"com.jimistore"};

}
