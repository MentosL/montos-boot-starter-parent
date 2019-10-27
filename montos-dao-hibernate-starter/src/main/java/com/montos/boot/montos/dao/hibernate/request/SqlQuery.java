package com.montos.boot.montos.dao.hibernate.request;

import com.montos.boot.montos.dao.api.request.Order;
import com.montos.boot.montos.dao.api.request.Query;

public class SqlQuery<T> extends Query<T> {
	
	private Class<T> outClass;
	
	public Class<T> getOutClass() {
		return outClass;
	}

	public SqlQuery<T> setOutClass(Class<T> outClass) {
		this.outClass = outClass;
		return this;
	}

	public static final <T> SqlQuery<T> create(SqlTarget sqlTarget, Integer pageNum, Integer pageSize , Order... orders){
		return (SqlQuery<T>)new SqlQuery<T>().setTarget(sqlTarget).setPageNum(pageNum).setPageSize(pageSize).setOrders(orders);
	}
	
	public static final <T> SqlQuery<T> create(SqlJoinTarget sqlJoin, Integer pageNum, Integer pageSize , Order... orders){
		return (SqlQuery<T>)new SqlQuery<T>().setTarget(sqlJoin).setPageNum(pageNum).setPageSize(pageSize).setOrders(orders);
	}
	
	public static final <T> SqlQuery<T> create(SqlJoinTarget sqlJoin, Integer pageNum, Integer pageSize , Class<T> outClass, Order... orders){
		return (SqlQuery<T>)new SqlQuery<T>().setOutClass(outClass).setTarget(sqlJoin).setPageNum(pageNum).setPageSize(pageSize).setOrders(orders);
	}

	

}