package com.montos.boot.montos.sliding.window.starter.core;

import com.montos.boot.montos.sliding.window.starter.handler.INoticeHandler;
import com.montos.boot.montos.sliding.window.starter.handler.IPublishHandler;

public interface IDispatcher {
	
	/**
	 * 删除topic
	 * @param topic
	 * @return 
	 */
	public IDispatcher createTopic(Topic topic);
	
	/**
	 * 删除topic
	 * @param topicKey
	 * @return 
	 */
	public IDispatcher deleteTopic(String topicKey);
	
	/**
	 * 发布订阅
	 * @param subscriber
	 */
	public IDispatcher subscribe(ISubscriber subscriber);
	
	/**
	 * 退订
	 * @param subscriber
	 * @return
	 */
	public IDispatcher unsubscribe(ISubscriber subscriber);
	
	/**
	 * 发布计数
	 * @param event
	 */
	public IDispatcher publish(IPublishEvent<?> event);
	
	/**
	 * 添加发布处理器
	 * @param publishHandler
	 */
	public IDispatcher addPublishHandler(IPublishHandler publishHandler);
	
	/**
	 * 添加通知处理器
	 * @param noticeHandler
	 */
	public IDispatcher addNoticeHandler(INoticeHandler noticeHandler);
	
	/**
	 * 创建计数器
	 * @param topic 主题
	 * @return
	 */
	public IDispatcher createCounter(Topic topic);

	/**
	 * 删除计数器
	 * @param key
	 * @return
	 */
	public IDispatcher deleteCounter(String key);

}
