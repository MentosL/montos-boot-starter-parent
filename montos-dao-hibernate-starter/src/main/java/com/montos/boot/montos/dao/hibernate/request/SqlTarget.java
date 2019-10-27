package com.montos.boot.montos.dao.hibernate.request;

import com.montos.boot.montos.dao.api.request.Filter;
import com.montos.boot.montos.dao.api.request.Target;
import com.montos.boot.montos.dao.hibernate.enums.JoinType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SqlTarget extends Target {
	
	List<SqlTarget> joinList = new ArrayList<SqlTarget>();
	
	JoinType joinType;
	
	SqlTarget preTarget;
	
	String preKey;
	
	String selfKey;
	
	Serializable[] groupFieldNames;
	
	public SqlTarget(){}
	
	public static final SqlTarget create(Class<?> entityClass, Filter filter, Serializable... outFieldNames){
		return new SqlTarget().setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames);
	}
	
	public static final SqlTarget createJoin(JoinType joinType, String preKey, String selfKey, Class<?> entityClass, Filter filter, Serializable... outFieldNames){
		return new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(joinType);
	}
	
	public SqlTarget innerJoin(String preKey, String selfKey, Class<?> entityClass, Filter filter, Serializable... outFieldNames){
		joinList.add(new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(JoinType.inner));
		return this;
	}
	
	public SqlTarget leftJoin(String preKey, String selfKey, Class<?> entityClass, Filter filter, Serializable... outFieldNames){
		joinList.add(new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(JoinType.left));
		return this;
	}
	
	public SqlTarget rightJoin(String preKey, String selfKey, Class<?> entityClass, Filter filter, Serializable... outFieldNames){
		joinList.add(new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(JoinType.right));
		return this;
	}
	
	public SqlTarget fullJoin(String preKey, String selfKey, Class<?> entityClass, Filter filter, Serializable... outFieldNames){
		joinList.add(new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(JoinType.full));
		return this;
	}
	
	public SqlTarget join(SqlTarget sqlTarget){
		joinList.add(sqlTarget);
		return this;
		
	}

	public List<SqlTarget> getJoinList() {
		return joinList;
	}

	public SqlTarget setJoinList(List<SqlTarget> joinList) {
		this.joinList = joinList;
		return this;
	}

	public String getPreKey() {
		return preKey;
	}

	public SqlTarget setPreKey(String preKey) {
		this.preKey = preKey;
		return this;
	}

	public String getSelfKey() {
		return selfKey;
	}

	public SqlTarget setSelfKey(String selfKey) {
		this.selfKey = selfKey;
		return this;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public SqlTarget setJoinType(JoinType joinType) {
		this.joinType = joinType;
		return this;
	}

	@Override
	public SqlTarget setEntityClass(Class<?> entityClass) {
		super.setEntityClass(entityClass);
		return this;
	}

	@Override
	public SqlTarget setFilter(Filter filter) {
		super.setFilter(filter);
		return this;
	}

	public SqlTarget getPreTarget() {
		return preTarget;
	}

	public SqlTarget setPreTarget(SqlTarget preTarget) {
		this.preTarget = preTarget;
		return this;
	}

	@Override
	public SqlTarget setOutFieldNames(Serializable... fieldNames) {
		super.setOutFieldNames(fieldNames);
		return this;
	}

	public Serializable[] getGroupFieldNames() {
		return groupFieldNames;
	}

	public SqlTarget setGroupFieldNames(Serializable... groupFieldNames) {
		this.groupFieldNames = groupFieldNames;
		return this;
	}
	
	

}
