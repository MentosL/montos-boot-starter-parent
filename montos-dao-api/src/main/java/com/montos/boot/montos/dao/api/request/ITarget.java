package com.montos.boot.montos.dao.api.request;

import java.io.Serializable;

public interface ITarget {
	
	public Class<?> getEntityClass();
		
	public Serializable[] getOutFieldNames();
	
	public Filter getFilter();

}
