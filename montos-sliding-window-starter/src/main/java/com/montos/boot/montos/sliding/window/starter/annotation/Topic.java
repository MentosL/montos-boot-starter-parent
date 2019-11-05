package com.montos.boot.montos.sliding.window.starter.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Topic {
	
	String value() default "";
	
	TimeUnit timeUnit() default TimeUnit.SECONDS;
	
	String condition() default "true";
	
	String num() default "1";
	
	int capacity() default 1440;
	
	Class<?> valueType() default Integer.class;

}
