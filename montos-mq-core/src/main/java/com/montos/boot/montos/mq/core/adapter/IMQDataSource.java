package com.montos.boot.montos.mq.core.adapter;

public interface IMQDataSource {
	
	public static final String DEFAULT = "default";
	
	/**
	 * 获取MQ数据源类型
	 * @return
	 */
	public String getType();
	
	/**
	 * 获取MQ数据源标识
	 * @return
	 */
	public String getKey();
	
	/**
	 * 获取数据源的发送适配器
	 * @return
	 */
	public IMQSender getMQSender();
	
	/**
	 * 获取数据源的消息监听器
	 * @return
	 */
	public IMQListener getMQListener();

}
