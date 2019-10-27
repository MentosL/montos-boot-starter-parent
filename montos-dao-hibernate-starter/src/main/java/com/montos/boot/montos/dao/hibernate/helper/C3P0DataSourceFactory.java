package com.montos.boot.montos.dao.hibernate.helper;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.montos.boot.montos.dao.api.config.MontosDataSourceProperties;
import com.montos.boot.montos.dao.api.core.IMontosDataSourceFactory;

import java.beans.PropertyVetoException;

public class C3P0DataSourceFactory implements IMontosDataSourceFactory<ComboPooledDataSource> {

	MontosDataSourceProperties montosDataSourceProperties;

	@Override
	public ComboPooledDataSource getObject() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(montosDataSourceProperties.getDriverClass());
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}

		dataSource.setJdbcUrl(montosDataSourceProperties.getJdbcUrl());
		dataSource.setUser(montosDataSourceProperties.getUser());
		dataSource.setPassword(montosDataSourceProperties.getPassword());

		dataSource.setMinPoolSize(montosDataSourceProperties.getMinPoolSize());
		dataSource.setMaxPoolSize(montosDataSourceProperties.getMaxPoolSize());
		dataSource.setMaxIdleTime(montosDataSourceProperties.getMaxIdleTime());
		dataSource.setAcquireIncrement(montosDataSourceProperties.getAcquireIncrement());
		dataSource.setMaxStatements(montosDataSourceProperties.getMaxStatements());
		dataSource.setInitialPoolSize(montosDataSourceProperties.getInitialPoolSize());
		dataSource.setIdleConnectionTestPeriod(montosDataSourceProperties.getIdleConnectionTestPeriod());
		dataSource.setAcquireRetryAttempts(montosDataSourceProperties.getAcquireRetryAttempts());
		dataSource.setBreakAfterAcquireFailure(montosDataSourceProperties.getBreakAfterAcquireFailure());
		dataSource.setTestConnectionOnCheckout(montosDataSourceProperties.getTestConnectionOnCheckout());

		return dataSource;
	}

	@Override
	public Class<?> getObjectType() {
		return ComboPooledDataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public IMontosDataSourceFactory<ComboPooledDataSource> setNemoDataSourceProperties(
			MontosDataSourceProperties nemoDataSourceProperties) {
		this.montosDataSourceProperties = nemoDataSourceProperties;
		return this;
	}
}
