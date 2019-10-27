package com.montos.boot.montos.mq.rocketmq.starter.adapter;

import java.util.HashMap;
import java.util.Map;

public class RocketMQProperties {
	
	String type="normal";
	
	String key;
	
	String url;
	
	String user;
	
	String password;
	
	String producerId;
	
	String consumerId;
	
	Long sendTimeOut=3000l;
	
	Map<String, String> topicMap = new HashMap<String, String>();
	
	Map<String, String> extend = new HashMap<String, String>();

	public String getUrl() {
		return url;
	}

	public RocketMQProperties setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getUser() {
		return user;
	}

	public RocketMQProperties setUser(String user) {
		this.user = user;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public RocketMQProperties setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getProducerId() {
		return producerId;
	}

	public RocketMQProperties setProducerId(String producerId) {
		this.producerId = producerId;
		return this;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public RocketMQProperties setConsumerId(String consumerId) {
		this.consumerId = consumerId;
		return this;
	}

	public Long getSendTimeOut() {
		return sendTimeOut;
	}

	public RocketMQProperties setSendTimeOut(Long sendTimeOut) {
		this.sendTimeOut = sendTimeOut;
		return this;
	}

	public String getType() {
		return type;
	}

	public RocketMQProperties setType(String type) {
		this.type = type;
		return this;
	}

	public String getKey() {
		return key;
	}

	public RocketMQProperties setKey(String key) {
		this.key = key;
		return this;
	}

	public Map<String, String> getTopicMap() {
		return topicMap;
	}

	public RocketMQProperties setTopicMap(Map<String, String> topicMap) {
		this.topicMap = topicMap;
		return this;
	}

	public Map<String, String> getExtend() {
		return extend;
	}

	public RocketMQProperties setExtend(Map<String, String> extend) {
		this.extend = extend;
		return this;
	}
	
}