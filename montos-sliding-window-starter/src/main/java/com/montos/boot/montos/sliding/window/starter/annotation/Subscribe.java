package com.montos.boot.montos.sliding.window.starter.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscribe {
	
	String value();
	
	TimeUnit timeUnit() default TimeUnit.SECONDS;
	
	int length() default 300;
	
	long interval() default 0;
	
	long start() default 0;
	
	String condition() default "true";
	
	Class<?> valueType() default Integer.class;

}
