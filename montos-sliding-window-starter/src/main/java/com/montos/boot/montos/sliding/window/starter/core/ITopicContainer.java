package com.montos.boot.montos.sliding.window.starter.core;

import java.util.Collection;

/**
 * topic容器
 * @author chenqi
 * @Date 2018年9月4日
 *
 */
public interface ITopicContainer {
	
	/**
	 * 获取topic集合
	 * @param method
	 * @param targetClass
	 * @return
	 */
	public Collection<Topic> listTopic();
	
	/**
	 * 创建一个topic
	 * @param topic
	 */
	public ITopicContainer createTopic(Topic topic);
	
	/**
	 * 删除一个topic
	 * @param topicKey
	 */
	public ITopicContainer deleteTopic(String topicKey);
	
	/**
	 * 获取一个topic
	 * @param topicKey
	 * @return
	 */
	public Topic getTopic(String topicKey);
	
	

}