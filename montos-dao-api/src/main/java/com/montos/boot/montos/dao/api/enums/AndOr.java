package com.jimistore.boot.nemo.dao.api.enums;

public enum AndOr{
	
	where("where",""),
	and("and",""),
	or("or","");

	String code;
	
	String alias;

	private AndOr(String code, String alias) {
		this.code = code;
		this.alias = alias;
	}

	public String getCode() {
		return code;
	}

	public String getAlias() {
		return alias;
	}
}
