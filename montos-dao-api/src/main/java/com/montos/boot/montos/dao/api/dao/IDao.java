package com.montos.boot.montos.dao.api.dao;

import com.montos.boot.montos.dao.api.request.IQuery;

import java.util.List;

public interface IDao {
	
	/**
	 * 创建一个对象
	 * @param entity
	 * @return
	 */
	public <T> T create(T entity);
	
	/**
	 * 删除一些对象
	 * @param query
	 * @return
	 */
	public <T> List<T> delete(IQuery<T> query);
	
	/**
	 * 删除一些对象
	 * @param entity
	 * @return
	 */
	public <T> T delete(T entity);
	
//	/**
//	 * 更新对象的部分数据
//	 * @param entityClass 实体类型
//	 * @param filter 过滤条件
//	 * @param entity 被更新的对象的键值对
//	 * @return
//	 */
//	public <T> List<T> update(Class<T> entityClass, Filter filter, Map<String,Object> entity);
	
	/**
	 * 更新对象
	 * @param entity 新的数据对象
	 * @return
	 */
	public <T> T update(T entity);
	
	/**
	 * 批量查询
	 * @param query 输出实体类型
	 * @return
	 */
	public <T> List<T> list(IQuery<T> query);
	
	
	/**
	 * 查询单个
	 * @param query
	 * @return
	 */
	public <T> T get(IQuery<T> query);

}
