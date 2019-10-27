package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.core.helper.Context;
import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.dao.hibernate.annotation.DataSource;
import com.montos.boot.montos.dao.hibernate.config.MutilDataSourceProperties;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Order(100)
public class MutilDaoAccessAspect {

	private final Logger log = Logger.getLogger(getClass());

	Map<Class<?>, DataSource> buffer = new HashMap<Class<?>, DataSource>();

	MutilSessionFactory mutilSessionFactory;

	public MutilDaoAccessAspect setMutilSessionFactory(MutilSessionFactory mutilSessionFactory) {
		this.mutilSessionFactory = mutilSessionFactory;
		return this;
	}

	@Pointcut("@within(com.montos.boot.montos.dao.hibernate.annotation.DataSource))")
	public void dataSource() {
	}

	@Pointcut("@within(org.springframework.transaction.annotation.Transactional)")
	public void transaction() {
	}

	@Before("dataSource()")
	public void beforeDataSource(JoinPoint joinPoint) throws Throwable {
		Class<?> clazz = joinPoint.getTarget().getClass();
		DataSource dataSource = buffer.get(clazz);
		if (dataSource == null) {
			dataSource = AnnotationUtil.getAnnotation(clazz, DataSource.class);
			buffer.put(clazz, dataSource);
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("init datasource key[%s]", dataSource != null ? dataSource.value() : "null"));
		}
		String ds = null;
		if (dataSource != null) {
			ds = dataSource.value();
		}
		this.setDataSourceKey(ds);
	}

	@Before("transaction()")
	public void beforeTransaction(JoinPoint joinPoint) throws Throwable {
		Class<?> clazz = joinPoint.getTarget().getClass();
		Transactional transactional = AnnotationUtil.getAnnotation(clazz, Transactional.class);
		if (log.isDebugEnabled()) {
		}

		String ds = null;
		if (transactional != null) {
			ds = transactional.value();
		}
		this.setDataSourceKey(ds);
	}

	private void setDataSourceKey(String dataSource) {
		if (dataSource != null && dataSource.trim().length() > 0) {
			Context.put(MutilDataSourceProperties.DATASROUCE_KEY, dataSource);
		} else {
//			Context.put(MutilDataSourceProperties.DATASROUCE_KEY, MutilDataSourceProperties.DEFAULT_DATASOURCE);
		}

	}

}
