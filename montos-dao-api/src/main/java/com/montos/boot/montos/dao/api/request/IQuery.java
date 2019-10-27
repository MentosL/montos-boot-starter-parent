package com.jimistore.boot.nemo.dao.api.request;

public interface IQuery<T> {
	
	/**
	 * 获取查询目标
	 * @return
	 */
	public ITarget getTarget();
	
	/**
	 * 设置查询目标
	 * @return
	 */
	public IQuery<T> setTarget(ITarget target);

	/**
	 * 获取页码
	 * @return
	 */
	public Integer getPageNum();
	
	/**
	 * 获取每页数量 
	 */
	public Integer getPageSize();
	
	/**
	 * 获取排序
	 * @return
	 */
	public Order[] getOrders();
	
}
