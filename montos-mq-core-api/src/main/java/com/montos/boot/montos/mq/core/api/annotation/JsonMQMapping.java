package com.montos.boot.montos.mq.core.api.annotation;

import com.montos.boot.montos.mq.core.api.enums.QueueType;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonMQMapping {
	
	public static final String DEFAULT_TAG="*";
	
	QueueType type() default QueueType.Queue;
	
	String value() default "";
	
	String tag() default DEFAULT_TAG;
	
	String delay() default "0";
	
	String key() default "";
	
	String shardingKey() default "";

}
