package com.montos.boot.montos.sliding.window.starter.core;

import java.util.List;

public interface IChannelContainer {
	
	/**
	 * 放入发布的主题
	 * @param topic
	 * @return
	 */
	public IChannelContainer put(String topic);
	
	/**
	 * 删除发布的主题
	 * @param key
	 * @return
	 */
	public IChannelContainer delete(String key);
	
	/**
	 * 删除订阅者
	 * @param key
	 * @return
	 */
	public IChannelContainer delete(ISubscriber subscriber);
	
	/**
	 * 放入订阅的订阅者
	 * @param subscribe 订阅者
	 */
	public IChannelContainer put(ISubscriber subscriber);
	
	/**
	 * 获取所有通道
	 * @return
	 */
	public List<Channel> listAllChannel();

}
