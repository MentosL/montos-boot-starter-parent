package com.montos.boot.montos.dao.hibernate.annotation;

import com.montos.boot.montos.dao.hibernate.config.MutilDataSourceProperties;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

	String value() default MutilDataSourceProperties.DEFAULT_DATASOURCE;
	
}
