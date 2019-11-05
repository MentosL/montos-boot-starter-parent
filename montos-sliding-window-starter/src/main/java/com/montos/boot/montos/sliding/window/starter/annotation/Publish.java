package com.montos.boot.montos.sliding.window.starter.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Publish {
	
	Topic[] value() default {};
	
	String alias() default "";

}
