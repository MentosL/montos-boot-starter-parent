package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.dao.api.core.IMontosDataSourceRegister;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源匹配器
 * 
 * @author chenqi
 * @date 2019年7月12日
 *
 */
public class DataSourceSelector implements InitializingBean {

	Map<String, IMontosDataSourceRegister> registerMap = new HashMap<String, IMontosDataSourceRegister>();

	List<IMontosDataSourceRegister> nemoDataSourceRegisterList;

	public DataSourceSelector setNemoDataSourceRegisterList(List<IMontosDataSourceRegister> nemoDataSourceRegisterList) {
		this.nemoDataSourceRegisterList = nemoDataSourceRegisterList;
		return this;
	}

	public Class<?> getNemoDataSourceFactoryClass(String key) {
		if (key == null) {
			key = C3P0DataSourceRegister.KEY;
		}
		IMontosDataSourceRegister register = registerMap.get(key);
		if (register == null) {
			throw new RuntimeException("can not find register[%s], please check properties of db");
		}
		Class<?> clazz = register.getNemoDataSourceFactoryClass();
		if (clazz == null) {
			throw new RuntimeException("nemo data source factory class can not be null");
		}
		if (!IMontosDataSourceRegister.class.isAssignableFrom(clazz)) {
			throw new RuntimeException("nemo data source factory class must instanceof INemoDataSourceFactory");
		}
		return clazz;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		for (IMontosDataSourceRegister register : nemoDataSourceRegisterList) {
			registerMap.put(register.getKey(), register);
		}
	}

}