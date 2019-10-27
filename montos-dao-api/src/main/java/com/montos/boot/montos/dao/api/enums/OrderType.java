package com.jimistore.boot.nemo.dao.api.enums;

public enum OrderType {

	asc("asc","升序"),
	desc("desc","降序");
	
	String code;
	
	String alias;

	private OrderType(String code, String alias) {
		this.code = code;
		this.alias = alias;
	}

	public String getCode() {
		return code;
	}

	public String getAlias() {
		return alias;
	}
	
	public Object formatValue(Object obj){
		return obj;
	}
}
