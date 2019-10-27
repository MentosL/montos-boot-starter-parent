package com.montos.boot.montos.dao.api.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XssIgnoreField {
	
	String[] value() default {};

}
