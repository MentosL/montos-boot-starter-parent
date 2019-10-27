package com.montos.boot.montos.dao.hibernate.enums;

public enum JoinType{
	
	left("left join",""),
	right("right join",""),
	full("full outer join",""),
	inner("inner join","");

	String code;
	
	String alias;

	private JoinType(String code, String alias) {
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
