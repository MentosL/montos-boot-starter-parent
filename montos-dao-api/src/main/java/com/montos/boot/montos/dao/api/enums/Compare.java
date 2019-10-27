package com.jimistore.boot.nemo.dao.api.enums;

public enum Compare{

	eq("=","等于"),
	neq("<>","不等于"),
	lt("<","等于"),
	lte("<=","等于"),
	gt(">","等于"),
	gte(">=","等于"),
	in("in","在范围内"),
	nin("not in","不在范围内"),
	nl("null","为空的"),
	nnl("notnull","不为空的"),
	lelike("like","左模糊匹配"),
	rilike("like","右模糊匹配"),
	like("like","模糊匹配");
	
	String code;
	
	String alias;
	
	boolean number;

	private Compare(String code, String alias) {
		this.code = code;
		this.alias = alias;
	}

	private Compare(String code, String alias, boolean number) {
		this.code = code;
		this.alias = alias;
		this.number = number;
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

	public boolean isNumber() {
		return number;
	}
	
}