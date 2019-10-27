package com.montos.boot.montos.dao.hibernate.helper;

import com.montos.boot.montos.dao.api.config.MontosDataSourceProperties;
import com.montos.boot.montos.dao.hibernate.config.HibernateProperties;
import org.hibernate.cfg.NamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class BaseSessionFactory extends LocalSessionFactoryBean {

	String key;

	HibernateProperties hibernatePropertie;

	MontosDataSourceProperties dataSourcePropertie;

	DataSource dataSource;

	NamingStrategy namingStrategy;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		super.setDataSource(dataSource);
	}

	@Autowired
	@Override
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
		super.setNamingStrategy(namingStrategy);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}

	public BaseSessionFactory() {
		super();
	}

	public void setHibernatePropertie(HibernateProperties hibernatePropertie) {
		this.hibernatePropertie = hibernatePropertie;

	}

	public void setDataSourcePropertie(MontosDataSourceProperties dataSourcePropertie) {
		this.dataSourcePropertie = dataSourcePropertie;
	}

	@Override
	public void afterPropertiesSet() throws IOException {

		super.setPackagesToScan(hibernatePropertie.getPackagesToScan());

		this.getHibernateProperties().setProperty("hibernate.show_sql", hibernatePropertie.getShow_sql());
		this.getHibernateProperties().setProperty("hibernate.hbm2ddl.auto", hibernatePropertie.getHbm2ddl().getAuto());

		String driverClass = dataSourcePropertie.getDriverClass();
		String characterEncoding = dataSourcePropertie.getCharacterEncoding();
		this.getHibernateProperties().setProperty("connection.characterEncoding", characterEncoding);

		try {
			if (driverClass.indexOf("mysql") >= 0) {
				this.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
			} else if (driverClass.indexOf("oracle") >= 0) {
				this.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
			} else if (driverClass.indexOf("sqlserver") >= 0 || driverClass.indexOf("jtds") >= 0) {
				this.getHibernateProperties().setProperty("hibernate.dialect",
						"org.hibernate.dialect.SQLServerDialect");
			}
		} catch (Exception e) {

		}

		String dialect = hibernatePropertie.getDialect();
		if (dialect != null && dialect.trim().length() > 0) {
			this.getHibernateProperties().setProperty("hibernate.dialect", dialect);
		}

		super.afterPropertiesSet();
	}

	public String getKey() {
		return key;
	}

	public HibernateProperties getHibernatePropertie() {
		return hibernatePropertie;
	}

	public MontosDataSourceProperties getDataSourcePropertie() {
		return dataSourcePropertie;
	}

	public BaseSessionFactory setKey(String key) {
		this.key = key;
		return this;
	}
}
