package com.montos.boot.montos.mq.core.helper;

import com.montos.boot.montos.mq.core.adapter.IMQDataSource;

import java.util.List;

public class MQDataSourceGroup {
	
	String type;
	
	List<IMQDataSource> dataSourceList;

	public String getType() {
		return type;
	}

	public MQDataSourceGroup setType(String type) {
		this.type = type;
		return this;
	}

	public List<IMQDataSource> getDataSourceList() {
		return dataSourceList;
	}

	public MQDataSourceGroup setDataSourceList(List<IMQDataSource> dataSourceList) {
		this.dataSourceList = dataSourceList;
		return this;
	}

}
