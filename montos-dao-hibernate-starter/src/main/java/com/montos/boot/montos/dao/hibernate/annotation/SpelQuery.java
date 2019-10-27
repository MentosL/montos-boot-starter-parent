package com.montos.boot.montos.dao.hibernate.annotation;

import com.montos.boot.montos.dao.hibernate.enums.QueryType;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpelQuery {
	
	String[] value() default "\"\"";
	
	String pageSize() default "#pageSize==null?10:#pageSize";
	
	String pageNum() default "#pageNum==null?1:#pageNum";
	
	QueryType type() default QueryType.HQL;

}
