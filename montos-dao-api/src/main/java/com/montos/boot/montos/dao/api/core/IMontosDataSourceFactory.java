package com.montos.boot.montos.dao.api.core;

import com.montos.boot.montos.dao.api.config.MontosDataSourceProperties;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

public interface IMontosDataSourceFactory<T extends DataSource> extends FactoryBean<T> {

	/**
	 * 创建数据源
	 * 
	 * @param properties
	 * @return
	 */
	public IMontosDataSourceFactory<T> setNemoDataSourceProperties(MontosDataSourceProperties properties);

}
