package com.montos.boot.montos.dao.api.request;

public class Query<T> implements IQuery<T> {
	
	ITarget target;
	
	private Integer pageNum;
	
	private Integer pageSize=12;
	
	Order[] orders;
	
	protected Query(){}
	
	public static <T> Query<T> create(Class<T> entityClass, Filter filter, Integer pageNum, Integer pageSize , Order... orders){
		return new Query<T>()
				.setPageNum(pageNum)
				.setPageSize(pageSize)
				.setOrders(orders)
				.setTarget(new Target().setEntityClass(entityClass).setFilter(filter));
	}
	
	public static <T> Query<T> create(Class<T> entityClass, Filter filter, Order... orders){
		return new Query<T>()
				.setOrders(orders)
				.setTarget(new Target().setEntityClass(entityClass).setFilter(filter));
	}
	
	public static <T> Query<T> create(ITarget target, Integer pageNum, Integer pageSize , Order... orders){
		return new Query<T>()
				.setPageNum(pageNum)
				.setPageSize(pageSize)
				.setOrders(orders)
				.setTarget(target);
	}
	
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static Query create(Filter filter, Order... orders){
		return new Query().setTarget(new Target().setFilter(filter)).setOrders(orders);
	}
	
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static Query create(Filter filter, Integer pageNum, Integer pageSize , Order... orders){
		return new Query().setTarget(new Target().setFilter(filter)).setPageNum(pageNum).setPageSize(pageSize).setOrders(orders);
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public Query<T> setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Query<T> setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public Order[] getOrders() {
		return orders;
	}

	public Query<T> setOrders(Order... orders) {
		this.orders = orders;
		return this;
	}

	@Override
	public ITarget getTarget() {
		return target;
	}

	@Override
	public Query<T> setTarget(ITarget target) {
		this.target=target;
		return this;
	}

}
