package com.montos.boot.montos.sliding.window.starter.core;

import java.util.List;

public class NoticeEvent<T extends Number> implements INoticeEvent<T> {
	
	String topicKey;
	
	Long time;
	
	List<T> value;
	
	ISubscriber subscriber;

	public String getTopicKey() {
		return topicKey;
	}

	public NoticeEvent<T> setTopicKey(String topicKey) {
		this.topicKey = topicKey;
		return this;
	}

	public Long getTime() {
		return time;
	}

	public NoticeEvent<T> setTime(Long time) {
		this.time = time;
		return this;
	}

	public List<T> getValue() {
		return value;
	}

	public NoticeEvent<T> setValue(List<T> value) {
		this.value = value;
		return this;
	}

	public ISubscriber getSubscriber() {
		return subscriber;
	}

	public NoticeEvent<T> setSubscriber(ISubscriber subscriber) {
		this.subscriber = subscriber;
		return this;
	}
	
	
	

}
