package com.montos.boot.montos.dao.api.request;

import java.io.Serializable;

public class Target implements ITarget {
	
	Class<?> entityClass;
	
	Serializable[] outFieldNames;
	
	Filter filter;

	@Override
	public Class<?> getEntityClass() {
		return entityClass;
	}

	public Serializable[] getOutFieldNames() {
		return outFieldNames;
	}

	public Target setOutFieldNames(Serializable... outFieldNames) {
		this.outFieldNames = outFieldNames;
		return this;
	}

	public Target setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
		return this;
	}

	public Filter getFilter() {
		return filter;
	}

	public Target setFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

}
