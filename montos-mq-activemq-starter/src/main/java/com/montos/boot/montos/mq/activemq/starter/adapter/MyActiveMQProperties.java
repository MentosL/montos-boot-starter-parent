package com.montos.boot.montos.mq.activemq.starter.adapter;

import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;

public class MyActiveMQProperties extends ActiveMQProperties {
	
	String type;
	
	String key;

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public MyActiveMQProperties setType(String type) {
		this.type = type;
		return this;
	}

	public MyActiveMQProperties setKey(String key) {
		this.key = key;
		return this;
	}

}
