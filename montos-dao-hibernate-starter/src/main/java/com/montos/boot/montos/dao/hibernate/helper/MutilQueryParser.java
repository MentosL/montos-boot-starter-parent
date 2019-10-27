package com.montos.boot.montos.dao.hibernate.helper;

public class MutilQueryParser extends QueryParser {
	
	MutilSessionFactory mutilSessionFactory;

	public MutilQueryParser setMutilSessionFactory(MutilSessionFactory mutilSessionFactory) {
		this.mutilSessionFactory = mutilSessionFactory;
		return this;
	}
	
	public HibernateNamingStrategy getHibernateNamingStrategy(){
		return MutilSessionFactory.getHibernateNamingStrategy();
	}
	

}
