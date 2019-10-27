package com.montos.boot.montos.dao.hibernate.config;

import com.montos.boot.montos.dao.api.config.MontosDataSourceProperties;
import com.montos.boot.montos.dao.api.core.IMontosDataSourceRegister;
import com.montos.boot.montos.dao.api.dao.IDao;
import com.montos.boot.montos.dao.api.validator.IQueryValidator;
import com.montos.boot.montos.dao.api.validator.IXSSValidator;
import com.montos.boot.montos.dao.hibernate.dao.MutilHibernateQueryDao;
import com.montos.boot.montos.dao.hibernate.helper.*;
import com.montos.boot.montos.dao.hibernate.validator.IInjectSqlValidator;
import com.montos.boot.montos.dao.hibernate.validator.InjectSqlValidator;
import com.montos.boot.montos.dao.hibernate.validator.XSSValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration
@EnableConfigurationProperties({ HibernateProperties.class, MontosDataSourceProperties.class,
		MutilDataSourceProperties.class })
@EnableTransactionManagement(proxyTargetClass = true)
@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true", matchIfMissing = true)
public class MontosDaoHibernateAutoConfiguration {

	HibernateProperties hibernateProperties;

	MontosDataSourceProperties dataSourceProperties;

	private MutilDataSourceProperties mutilDataSourceProperties;

	public MontosDaoHibernateAutoConfiguration(HibernateProperties hibernateProperties,
											   MontosDataSourceProperties dataSourceProperties, MutilDataSourceProperties mutilDataSourceProperties) {
		super();
		this.hibernateProperties = hibernateProperties;
		this.dataSourceProperties = dataSourceProperties;
		this.mutilDataSourceProperties = mutilDataSourceProperties;
		if (dataSourceProperties != null && dataSourceProperties.getJdbcUrl() != null) {
			this.mutilDataSourceProperties.getDatasource().put(MutilDataSourceProperties.DEFAULT_DATASOURCE,
					dataSourceProperties);
			this.mutilDataSourceProperties.getHibernate().put(MutilDataSourceProperties.DEFAULT_DATASOURCE,
					hibernateProperties);
		}
	}

	@Bean
	@ConditionalOnMissingBean(C3P0DataSourceRegister.class)
	public C3P0DataSourceRegister c3P0DataSourceRegister() {
		return new C3P0DataSourceRegister();
	}

	@Bean
	@ConditionalOnMissingBean(DataSourceSelector.class)
	public DataSourceSelector dataSourceSelector(@Lazy List<IMontosDataSourceRegister> montosDataSourceRegisterList) {
		return new DataSourceSelector().setNemoDataSourceRegisterList(montosDataSourceRegisterList);
	}

	@Bean
	@ConditionalOnMissingBean(MutilSessionFactory.class)
	public MutilSessionFactory mutilSessionFactory(DataSourceSelector dataSourceSelector) {
		return new MutilSessionFactory().setMutilDataSourceProperties(mutilDataSourceProperties)
				.setDataSourceSelector(dataSourceSelector);
	}

	@Bean
	@ConditionalOnMissingBean(MutilTransactionManager.class)
	public MutilTransactionManager mutilTransactionManager() {
		return new MutilTransactionManager();
	}

	@Bean
	@ConditionalOnMissingBean(MutilSessionFactoryHelper.class)
	public MutilSessionFactoryHelper mutilSessionFactoryHelper(MutilSessionFactory mutilSessionFactory,
			MutilTransactionManager mutilTransactionManager) {
		return new MutilSessionFactoryHelper().setMutilSessionFactory(mutilSessionFactory)
				.setMutilTransactionManager(mutilTransactionManager);
	}

	@Bean("db")
	@ConditionalOnMissingBean(MutilDataSourceHealthEndPoint.class)
	public MutilDataSourceHealthEndPoint mutilDataSourceHealthEndPoint(
			@Lazy List<BaseSessionFactory> sessionFactoryList) {
		return new MutilDataSourceHealthEndPoint().setSessionFactoryList(sessionFactoryList);
	}

	@Bean
	@ConditionalOnMissingBean(MutilDaoAccessAspect.class)
	public MutilDaoAccessAspect daoAccessAspect(MutilSessionFactory mutilSessionFactory) {
		return new MutilDaoAccessAspect().setMutilSessionFactory(mutilSessionFactory);
	}

	@Bean()
	@ConditionalOnMissingBean({ IQueryParser.class, IQueryValidator.class })
	public IQueryParser IQueryParser(MutilSessionFactory mutilSessionFactory) {
		return new MutilQueryParser().setMutilSessionFactory(mutilSessionFactory);
	}

	@Bean()
	@ConditionalOnMissingBean({ IInjectSqlValidator.class })
	public IInjectSqlValidator IInjectSqlValidator() {
		return new InjectSqlValidator();
	}

	@Bean()
	@ConditionalOnMissingBean({ IXSSValidator.class })
	public IXSSValidator IXSSValidator() {
		return new XSSValidator();
	}

	@Bean("hibernate")
	@ConditionalOnMissingBean(IDao.class)
	public MutilHibernateQueryDao MutilHibernateQueryDao(MutilSessionFactory sessionFactory, IQueryParser queryParser,
														 List<IXSSValidator> xssValidatorList, List<IInjectSqlValidator> queryValidatorList) {
		MutilHibernateQueryDao mutilHibernateQueryDao = new MutilHibernateQueryDao();
		mutilHibernateQueryDao.setMutilSessionFactory(sessionFactory).setQueryParser(queryParser)
				.setXssValidatorList(xssValidatorList).setQueryValidatorList(queryValidatorList);
		return mutilHibernateQueryDao;
	}

	@Bean()
	@ConditionalOnMissingBean(NotEmptySpelFunc.class)
	public NotEmptySpelFunc spelExtendFunc() {
		return new NotEmptySpelFunc();
	}

	@Bean()
	@ConditionalOnMissingBean(SpelExtendFuncAspect.class)
	public SpelExtendFuncAspect spelExtendFuncAspect(List<IInjectSqlValidator> queryValidatorList) {
		return new SpelExtendFuncAspect().setQueryValidatorList(queryValidatorList);
	}

	@Bean
	@ConditionalOnMissingBean(QueryHelper.class)
	public QueryHelper QueryHelper(MutilHibernateQueryDao mutilHibernateQueryDao,
			List<IInjectSqlValidator> queryValidatorList, List<ISpelExtendFunc> spelExtendFuncList) {
		return new QueryHelper().setQueryValidatorList(queryValidatorList)
				.setMutilHibernateQueryDao(mutilHibernateQueryDao).setSpelExtendFuncList(spelExtendFuncList);
	}

	@Bean
	@ConditionalOnMissingBean(QueryAspect.class)
	public QueryAspect QueryAspect(QueryHelper queryHelper) {
		return new QueryAspect().setQueryHelper(queryHelper);
	}

}
