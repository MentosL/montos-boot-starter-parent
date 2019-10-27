package com.montos.boot.montos.dao.api.core;

import com.montos.boot.montos.dao.api.config.MontosDataSourceProperties;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

public interface INemoDataSourceFactory<T extends DataSource> extends FactoryBean<T> {

	/**
	 * 创建数据源
	 * 
	 * @param properties
	 * @return
	 */
	public INemoDataSourceFactory<T> setNemoDataSourceProperties(MontosDataSourceProperties properties);

}
