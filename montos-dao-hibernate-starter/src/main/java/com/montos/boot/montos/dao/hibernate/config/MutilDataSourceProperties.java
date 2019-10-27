package com.montos.boot.montos.dao.hibernate.config;

import com.montos.boot.montos.dao.api.config.MontosDataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "nemo")
public class MutilDataSourceProperties {

	public static final String DEFAULT_DATASOURCE = "default";

	public static final String DATASROUCE_KEY = "datasource-key";
	public static final String DATASROUCE_TYPE_C3P0 = "C3P0";
	public static final String DATASROUCE_TYPE_HICARICP = "HICARICP";

	public String datasourceType = DATASROUCE_TYPE_C3P0;

	private Map<String, MontosDataSourceProperties> datasource = new HashMap<String, MontosDataSourceProperties>();

	private Map<String, HibernateProperties> hibernate = new HashMap<String, HibernateProperties>();

	public Map<String, MontosDataSourceProperties> getDatasource() {
		return datasource;
	}

	public MutilDataSourceProperties setDatasource(Map<String, MontosDataSourceProperties> datasource) {
		this.datasource = datasource;
		return this;
	}

	public Map<String, HibernateProperties> getHibernate() {
		return hibernate;
	}

	public MutilDataSourceProperties setHibernate(Map<String, HibernateProperties> hibernate) {
		this.hibernate = hibernate;
		return this;
	}

	public String getDatasourceType() {
		return datasourceType;
	}

	public MutilDataSourceProperties setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
		return this;
	}

}