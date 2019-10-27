package com.montos.boot.montos.dao.hibernate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hibernate")
public class HibernateProperties {

	public static class Hbm2ddl {

		String auto = "update";

		public String getAuto() {
			return auto;
		}

		public void setAuto(String auto) {
			this.auto = auto;
		}

	}

	String show_sql = "false";

	String packagesToScan = "*";

	String dialect = "";

	Boolean nameStrategyUnder = false;

	Hbm2ddl hbm2ddl = new Hbm2ddl();

	public String getShow_sql() {
		return show_sql;
	}

	public void setShow_sql(String show_sql) {
		this.show_sql = show_sql;
	}

	public String getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(String packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public Hbm2ddl getHbm2ddl() {
		return hbm2ddl;
	}

	public void setHbm2ddl(Hbm2ddl hbm2ddl) {
		this.hbm2ddl = hbm2ddl;
	}

	public Boolean getNameStrategyUnder() {
		return nameStrategyUnder;
	}

	public void setNameStrategyUnder(Boolean nameStrategyUnder) {
		this.nameStrategyUnder = nameStrategyUnder;
	}

}
