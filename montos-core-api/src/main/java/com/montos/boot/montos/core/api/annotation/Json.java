package com.jimistore.boot.nemo.core.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jimistore.boot.nemo.core.api.enums.JsonMode;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Json {
	JsonMode mode() default JsonMode.Exclusion;
	
	boolean notNull() default true;
	
	boolean deleted() default true;
}
