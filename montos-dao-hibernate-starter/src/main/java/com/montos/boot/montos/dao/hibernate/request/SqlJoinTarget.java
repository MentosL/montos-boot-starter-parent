package com.montos.boot.montos.dao.hibernate.request;

import com.montos.boot.montos.dao.api.request.Filter;
import com.montos.boot.montos.dao.api.request.ITarget;
import com.montos.boot.montos.dao.hibernate.enums.JoinType;

import java.io.Serializable;

public class SqlJoinTarget implements ITarget {
	
	JoinType joinType;
	
	SqlTarget parent;
	
	SqlTarget child;
	
	String parentKey;
	
	String childKey;
	
	SqlJoinTarget next;
	

	public JoinType getJoinType() {
		return joinType;
	}

	public SqlJoinTarget setJoinType(JoinType joinType) {
		this.joinType = joinType;
		return this;
	}

	public SqlTarget getParent() {
		return parent;
	}

	public SqlJoinTarget setParent(SqlTarget parent) {
		this.parent = parent;
		return this;
	}

	public SqlTarget getChild() {
		return child;
	}

	public SqlJoinTarget setChild(SqlTarget child) {
		this.child = child;
		return this;
	}

	public String getParentKey() {
		return parentKey;
	}

	public SqlJoinTarget setParentKey(String parentKey) {
		this.parentKey = parentKey;
		return this;
	}

	public String getChildKey() {
		return childKey;
	}

	public SqlJoinTarget setChildKey(String childKey) {
		this.childKey = childKey;
		return this;
	}

	@Override
	public Class<?> getEntityClass() {
		return parent.getEntityClass();
	}

	@Override
	public Serializable[] getOutFieldNames() {
		return parent.getOutFieldNames();
	}

	@Override
	public Filter getFilter() {
		return parent.getFilter();
	}

	public SqlJoinTarget getNext() {
		return next;
	}

	public SqlJoinTarget setNext(SqlJoinTarget next) {
		this.next = next;
		return this;
	}
	
	public SqlJoinTarget join(SqlJoinTarget next){
		if(this.getNext()!=null){
			next.setNext(this.getNext());
		}
		return this.setNext(next);
	}
	
	public SqlJoinTarget join(JoinType joinType, SqlTarget parent, String parentKey, SqlTarget child, String childKey){
		return this.join(SqlJoinTarget.create(joinType, parent, parentKey, child, childKey));
	}
	
	public static final SqlJoinTarget create(JoinType joinType, SqlTarget parent, String parentKey, SqlTarget child, String childKey){
		return new SqlJoinTarget()
				.setJoinType(joinType)
				.setParent(parent)
				.setParentKey(parentKey)
				.setChild(child)
				.setChildKey(childKey);
	}
	
	

}
