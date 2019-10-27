package com.montos.boot.montos.dao.hibernate.dao;

import com.montos.boot.montos.dao.api.dao.IDao;
import com.montos.boot.montos.dao.api.request.IQuery;
import com.montos.boot.montos.dao.api.validator.IXSSValidator;
import com.montos.boot.montos.dao.hibernate.helper.IQueryParser;
import com.montos.boot.montos.dao.hibernate.validator.IInjectSqlValidator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HibernateDao implements IDao {
	
	private SessionFactory sessionFactory;
	
	private IQueryParser queryParser;
	
	private List<IInjectSqlValidator> queryValidatorList;
	
	private List<IXSSValidator> xssValidatorList;

	public HibernateDao setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		return this;
	}

	public HibernateDao setQueryParser(IQueryParser queryParser) {
		this.queryParser = queryParser;
		return this;
	}

	public HibernateDao setXssValidatorList(List<IXSSValidator> xssValidatorList) {
		this.xssValidatorList = xssValidatorList;
		return this;
	}

	public HibernateDao setQueryValidatorList(List<IInjectSqlValidator> queryValidatorList) {
		this.queryValidatorList = queryValidatorList;
		return this;
	}

	public Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	@Override
	public <T> T create(T entity) {
		this.check(entity);
		this.getSession().save(entity);
		return entity;
	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Override
//	public <T> List<T> update(Class<T> entityClass, Filter filter, Map<String,Object> entity) {
//		List list = this.list(entityClass, filter);
//		for(Object obj:list){
//			this.getSession().update(this.inject(obj, entity));
//		}
//		return list;
//	}

	@Override
	public <T> T update(T entity) {
		this.check(entity);
		this.getSession().update(entity);
		return entity;
	}

	@SuppressWarnings("unused")
	private Object inject(Object old, Map<String,Object> entity) {
		for(Map.Entry<String, Object> entry:entity.entrySet()){
			String name = entry.getKey();
			String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
			try {
				Field field = old.getClass().getDeclaredField(name);
				old.getClass().getMethod(methodName, new Class[] { field.getType() })
						.invoke(old, new Object[] { entry.getValue() });
			} catch (Exception e) {
				throw new RuntimeException("找不到对应的属性", e);
			} 
		}
		return null;
	}
	
	private void check(Object obj){
		Iterator<IXSSValidator> it = xssValidatorList.iterator();
		while(it.hasNext()){
			it.next().check(obj);
		}
	}
	
	private void check(IQuery<?> query){
		Iterator<IInjectSqlValidator> it = queryValidatorList.iterator();
		while(it.hasNext()){
			it.next().check(query);
		}
	}

	@Override
	public <T> T delete(T entity) {
		this.getSession().delete(entity);
		return entity;
	}

	@Override
	public <T> List<T> delete(IQuery<T> query) {
		List<T> dataList = this.list(query);
		for(T t:dataList){
			this.getSession().delete(t);
		}
		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list(IQuery<T> query) {
		//校验参数
		this.check(query);
		return queryParser.parse(this.getSession(), query).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(IQuery<T> query) {
		//校验参数
		this.check(query);
		
		//查询
		List<T> list = queryParser.parse(this.getSession(), query).list();
		if(list!=null&&list.size()>0){
			return (T) list.get(0);
		}
		return null;
	}

}
