package com.jimistore.boot.nemo.dao.api.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class TBaseBean<T> extends BaseBean<T>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	public T id;

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}
	
}