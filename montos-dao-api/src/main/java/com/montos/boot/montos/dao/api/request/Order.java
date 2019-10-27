package com.jimistore.boot.nemo.dao.api.request;

import java.io.Serializable;

import com.jimistore.boot.nemo.dao.api.enums.OrderType;

public class Order {
	
	private OrderType orderType;
	
	private Serializable key;
	
	protected Order(){}
	
	public static Order create(String key, OrderType orderType){
		return new Order().setKey(key).setOrderType(orderType);
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public Order setOrderType(OrderType orderType) {
		this.orderType = orderType;
		return this;
	}

	public Serializable getKey() {
		return key;
	}

	public Order setKey(Serializable key) {
		this.key = key;
		return this;
	}

}
