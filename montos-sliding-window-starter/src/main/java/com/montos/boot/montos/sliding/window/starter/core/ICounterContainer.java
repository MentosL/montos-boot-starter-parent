package com.montos.boot.montos.sliding.window.starter.core;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ICounterContainer {
	
	/**
	 * 计数心跳
	 */
	public default void heartbeat() {}
	
	/**
	 * 创建计数器
	 * @param topic 主题
	 * @return
	 */
	public ICounterContainer createCounter(Topic topic);
	
	/**
	 * 删除计数器
	 * @param key 容器名称
	 * @param timeUnit 时间单位
	 * @param capacity 容量
	 * @param 数据类型
	 * @return
	 */
	public ICounterContainer deleteCounter(String key);
	
	/**
	 * 发布数据
	 * @param event
	 * @return
	 */
	public ICounterContainer publish(IPublishEvent<?> event);
	
	/**
	 * 获取窗口的数据
	 * @param key
	 * @param timeUnit
	 * @param length
	 */
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType);
	
	/**
	 * 根据指定时间获取窗口的数据
	 * @param timeUnit 窗口长度单位
	 * @param length 窗口长度
	 * @param valueType 数据类型
	 * @param timestamp 时间戳(ms)
	 */
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType, long timestamp);

	
}
