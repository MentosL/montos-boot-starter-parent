package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.dao.hibernate.annotation.SpelQuery;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

@Aspect
@Order(Integer.MAX_VALUE)
public class QueryAspect {
	
	private final Logger log = Logger.getLogger(getClass());
	
	QueryHelper queryHelper;

	public QueryAspect setQueryHelper(QueryHelper queryHelper) {
		this.queryHelper = queryHelper;
		return this;
	}

	@Pointcut("@annotation(com.montos.boot.montos.dao.hibernate.annotation.SpelQuery)")
	public void gquery(){
	}
	
	@Around("gquery()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		log.debug("request query");
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		SpelQuery query = AnnotationUtil.getAnnotation(method, SpelQuery.class);
		
		return queryHelper.query(query, method, joinPoint.getArgs());
	}
	
	
	
}

