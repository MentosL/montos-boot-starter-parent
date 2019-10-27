package com.montos.boot.montos.core.api.annotation;

import com.montos.boot.montos.core.api.enums.JsonMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Json {
	JsonMode mode() default JsonMode.Exclusion;
	
	boolean notNull() default true;
	
	boolean deleted() default true;
}
