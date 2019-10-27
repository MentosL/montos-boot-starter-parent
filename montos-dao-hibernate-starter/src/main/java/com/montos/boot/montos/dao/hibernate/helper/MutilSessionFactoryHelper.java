package com.montos.boot.montos.dao.hibernate.helper;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MutilSessionFactoryHelper implements BeanPostProcessor {

	private static final Logger log = Logger.getLogger(MutilSessionFactoryHelper.class);

	MutilSessionFactory mutilSessionFactory;

	MutilTransactionManager mutilTransactionManager;

	public MutilSessionFactoryHelper() {
	}

	public MutilSessionFactoryHelper setMutilTransactionManager(MutilTransactionManager mutilTransactionManager) {
		this.mutilTransactionManager = mutilTransactionManager;
		return this;
	}

	public MutilSessionFactoryHelper setMutilSessionFactory(MutilSessionFactory mutilSessionFactory) {
		this.mutilSessionFactory = mutilSessionFactory;
		return this;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof BaseSessionFactory) {
			if (log.isDebugEnabled()) {
				log.debug(
						String.format("post Process Before Initialization %s:%s", bean.getClass().getName(), beanName));
			}
			BaseSessionFactory baseSessionFactory = (BaseSessionFactory) bean;
			mutilSessionFactory.put(baseSessionFactory.getKey(), baseSessionFactory.getObject());
			mutilTransactionManager.putSessionFactory(baseSessionFactory);
		}
		return bean;
	}

}
