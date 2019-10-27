package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.core.helper.Context;
import com.montos.boot.montos.dao.api.config.MontosDataSourceProperties;
import com.montos.boot.montos.dao.hibernate.config.HibernateProperties;
import com.montos.boot.montos.dao.hibernate.config.MutilDataSourceProperties;
import org.hibernate.*;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings({ "deprecation", "serial", "rawtypes" })
public class MutilSessionFactory implements ApplicationContextAware, InitializingBean, SessionFactory {

	Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();

	static Map<String, HibernateNamingStrategy> hibernateNamingStrategyMap = new HashMap<String, HibernateNamingStrategy>();

	DataSourceSelector dataSourceSelector;

	private MutilDataSourceProperties mutilDataSourceProperties;

	ApplicationContext applicationContext;

	private static final Logger log = LoggerFactory.getLogger(MutilSessionFactory.class);

	public MutilSessionFactory() {
	}

	public MutilSessionFactory setDataSourceSelector(DataSourceSelector dataSourceSelector) {
		this.dataSourceSelector = dataSourceSelector;
		return this;
	}

	public MutilSessionFactory setMutilDataSourceProperties(MutilDataSourceProperties mutilDataSourceProperties) {
		this.mutilDataSourceProperties = mutilDataSourceProperties;
		return this;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;

		Map<String, MontosDataSourceProperties> dataSourceMap = mutilDataSourceProperties.getDatasource();
		Map<String, HibernateProperties> hibernateMap = mutilDataSourceProperties.getHibernate();
		for (Entry<String, MontosDataSourceProperties> entry : dataSourceMap.entrySet()) {

			MontosDataSourceProperties dataSourceProperties = entry.getValue();
			HibernateProperties hibernateProperties = hibernateMap.get(entry.getKey());

			HibernateNamingStrategy hibernateNamingStrategy = new HibernateNamingStrategy();
			hibernateNamingStrategy.setHibernateProperties(hibernateProperties);
			hibernateNamingStrategyMap.put(entry.getKey(), hibernateNamingStrategy);

			// 注册datasource
			String dataSourceName = String.format("nemo-data-source-%s", entry.getKey());
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
					.rootBeanDefinition(
							dataSourceSelector.getNemoDataSourceFactoryClass(dataSourceProperties.getType()))
					.addPropertyValue("nemoDataSourceProperties", dataSourceProperties);
			dlbf.registerBeanDefinition(dataSourceName, beanDefinitionBuilder.getBeanDefinition());

			// 注册sessionfactory
			String sessionFactoryName = String.format("nemo-mutil-session-factory-%s", entry.getKey());
			beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(BaseSessionFactory.class)
					.addPropertyValue("key", entry.getKey()).addPropertyReference("dataSource", dataSourceName)
					.addPropertyValue("namingStrategy", hibernateNamingStrategy)
					.addPropertyValue("hibernatePropertie", hibernateMap.get(entry.getKey()))
					.addPropertyValue("dataSourcePropertie", dataSourceProperties);
			dlbf.registerBeanDefinition(sessionFactoryName, beanDefinitionBuilder.getBeanDefinition());

		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
		// Bean的实例工厂
		DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) context.getBeanFactory();
		this.postProcessBeanFactory(dbf);
	}

	public void put(String key, SessionFactory value) {
		sessionFactoryMap.put(key, value);
	}

	public SessionFactory getSessionFactory() {
		String key = getDataSourceKey();
		if (log.isDebugEnabled()) {
			log.debug(String.format("request getSessionFactory by datasource[%s]", key));
		}

		SessionFactory sessionFactory = sessionFactoryMap.get(key);
		if (sessionFactory == null) {
			throw new RuntimeException(String.format("can not find datasource[%s] in configuration", key));
		}
		return sessionFactory;
	}

	private static String getDataSourceKey() {
		String key = (String) Context.get(MutilDataSourceProperties.DATASROUCE_KEY);
		if (key == null) {
			key = MutilDataSourceProperties.DEFAULT_DATASOURCE;
		}
		return key;
	}

	public static HibernateNamingStrategy getHibernateNamingStrategy() {
		String key = getDataSourceKey();
		HibernateNamingStrategy hibernateNamingStrategy = hibernateNamingStrategyMap.get(key);
		if (hibernateNamingStrategy == null) {
			throw new RuntimeException(String.format("can not find datasource[%s] in configuration", key));
		}
		return hibernateNamingStrategy;

	}

	@Override
	public Reference getReference() throws NamingException {

		return this.getSessionFactory().getReference();
	}

	@Override
	public SessionFactoryOptions getSessionFactoryOptions() {

		return this.getSessionFactory().getSessionFactoryOptions();
	}

	@Override
	public SessionBuilder withOptions() {

		return this.getSessionFactory().withOptions();
	}

	@Override
	public Session openSession() throws HibernateException {

		return this.getSessionFactory().openSession();
	}

	@Override
	public Session getCurrentSession() throws HibernateException {

		return this.getSessionFactory().getCurrentSession();
	}

	@Override
	public StatelessSessionBuilder withStatelessOptions() {

		return this.getSessionFactory().withStatelessOptions();
	}

	@Override
	public StatelessSession openStatelessSession() {

		return this.getSessionFactory().openStatelessSession();
	}

	@Override
	public StatelessSession openStatelessSession(Connection connection) {

		return this.getSessionFactory().openStatelessSession(connection);
	}

	@Override
	public ClassMetadata getClassMetadata(Class entityClass) {

		return this.getSessionFactory().getClassMetadata(entityClass);
	}

	@Override
	public ClassMetadata getClassMetadata(String entityName) {

		return this.getSessionFactory().getClassMetadata(entityName);
	}

	@Override
	public CollectionMetadata getCollectionMetadata(String roleName) {

		return this.getSessionFactory().getCollectionMetadata(roleName);
	}

	@Override
	public Map<String, ClassMetadata> getAllClassMetadata() {

		return this.getSessionFactory().getAllClassMetadata();
	}

	@Override
	public Map getAllCollectionMetadata() {

		return this.getSessionFactory().getAllCollectionMetadata();
	}

	@Override
	public Statistics getStatistics() {

		return this.getSessionFactory().getStatistics();
	}

	@Override
	public void close() throws HibernateException {

		this.getSessionFactory().close();
	}

	@Override
	public boolean isClosed() {

		return this.getSessionFactory().isClosed();
	}

	@Override
	public Cache getCache() {

		return this.getSessionFactory().getCache();
	}

	@Override
	public void evict(Class persistentClass) throws HibernateException {

		this.getSessionFactory().evict(persistentClass);
	}

	@Override
	public void evict(Class persistentClass, Serializable id) throws HibernateException {

		this.getSessionFactory().evict(persistentClass, id);
	}

	@Override
	public void evictEntity(String entityName) throws HibernateException {

		this.getSessionFactory().evictEntity(entityName);
	}

	@Override
	public void evictEntity(String entityName, Serializable id) throws HibernateException {

		this.getSessionFactory().evictEntity(entityName, id);
	}

	@Override
	public void evictCollection(String roleName) throws HibernateException {

		this.getSessionFactory().evictCollection(roleName);
	}

	@Override
	public void evictCollection(String roleName, Serializable id) throws HibernateException {

		this.getSessionFactory().evictCollection(roleName, id);
	}

	@Override
	public void evictQueries(String cacheRegion) throws HibernateException {

		this.getSessionFactory().evictQueries(cacheRegion);
	}

	@Override
	public void evictQueries() throws HibernateException {

		this.getSessionFactory().evictQueries();
	}

	@Override
	public Set<?> getDefinedFilterNames() {

		return this.getSessionFactory().getDefinedFilterNames();
	}

	@Override
	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {

		return this.getSessionFactory().getFilterDefinition(filterName);
	}

	@Override
	public boolean containsFetchProfileDefinition(String name) {

		return this.getSessionFactory().containsFetchProfileDefinition(name);
	}

	@Override
	public TypeHelper getTypeHelper() {

		return this.getSessionFactory().getTypeHelper();
	}

}
