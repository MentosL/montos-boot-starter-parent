package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.dao.api.core.IMontosDataSourceRegister;

public class C3P0DataSourceRegister implements IMontosDataSourceRegister {

	public static final String KEY = "c3p0";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Class<?> getNemoDataSourceFactoryClass() {
		return C3P0DataSourceFactory.class;
	}

}
