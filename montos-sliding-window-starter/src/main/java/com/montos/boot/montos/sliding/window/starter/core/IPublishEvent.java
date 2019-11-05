package com.montos.boot.montos.sliding.window.starter.core;

public interface IPublishEvent<T extends Number> extends IEvent<T> {
	
	/**
	 * 获取事件值
	 * @return
	 */
	public T getValue();

}
