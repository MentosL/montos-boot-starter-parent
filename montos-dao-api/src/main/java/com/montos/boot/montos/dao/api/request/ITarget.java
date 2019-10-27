package com.jimistore.boot.nemo.dao.api.request;

import java.io.Serializable;

public interface ITarget {
	
	public Class<?> getEntityClass();
		
	public Serializable[] getOutFieldNames();
	
	public Filter getFilter();

}
