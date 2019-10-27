package com.montos.boot.montos.mq.core.api.annotation;

import java.lang.annotation.*;

/**
 * 标记某接口是否需要支持异步调用
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonMQService {

    /**
     * 数据源.
     */
    String value() default "default";
}
