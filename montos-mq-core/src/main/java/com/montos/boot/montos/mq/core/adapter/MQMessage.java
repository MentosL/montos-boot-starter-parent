package com.montos.boot.montos.mq.core.adapter;

import com.montos.boot.montos.mq.core.api.enums.QueueType;

public class MQMessage {
	 
	/**
	 * 队列名称
	 */
	private String mQName;
	
	/**
	 * 队列标签
	 */
	private String tag;
	
	/**
	 * 消息内容
	 */
	private Object content;
	
	/**
	 * 发送消息类型
	 */
	private QueueType queueType;
	
	/**
	 * 延时时间
	 */
	private long delayTime;
	
	/**
	 * 消息标识
	 */
	private String key;
	
	/**
	 * 消息分区标识
	 */
	private String shardingKey;

	public String getmQName() {
		return mQName;
	}

	public MQMessage setmQName(String mQName) {
		this.mQName = mQName;
		return this;
	}

	public Object getContent() {
		return content;
	}

	public MQMessage setContent(Object content) {
		this.content = content;
		return this;
	}

	public QueueType getQueueType() {
		return queueType;
	}

	public MQMessage setQueueType(QueueType queueType) {
		this.queueType = queueType;
		return this;
	}

	public long getDelayTime() {
		return delayTime;
	}

	public MQMessage setDelayTime(long delayTime) {
		this.delayTime = delayTime;
		return this;
	}

	public String getTag() {
		return tag;
	}

	public MQMessage setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public String getKey() {
		return key;
	}

	public MQMessage setKey(String key) {
		this.key = key;
		return this;
	}

	public String getShardingKey() {
		return shardingKey;
	}

	public MQMessage setShardingKey(String shardingKey) {
		this.shardingKey = shardingKey;
		return this;
	}

	
}
