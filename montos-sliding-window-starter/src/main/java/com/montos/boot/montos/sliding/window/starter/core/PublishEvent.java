package com.montos.boot.montos.sliding.window.starter.core;

public class PublishEvent<T extends Number> implements IPublishEvent<T> {
	
	String topicKey;
	
	Long time;
	
	T value;

	public String getTopicKey() {
		return topicKey;
	}

	public PublishEvent<T> setTopicKey(String topicKey) {
		this.topicKey = topicKey;
		return this;
	}

	public Long getTime() {
		return time;
	}

	public PublishEvent<T> setTime(Long time) {
		this.time = time;
		return this;
	}

	public T getValue() {
		return value;
	}

	public PublishEvent<T> setValue(T value) {
		this.value = value;
		return this;
	}
	

}
