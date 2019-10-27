package com.montos.boot.montos.mq.rabbitmq.starter.adapter;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

public class RabbitProperties extends CachingConnectionFactory {

	String type;
	
	String key;

	public String getType() {
		return type;
	}

	public RabbitProperties setType(String type) {
		this.type = type;
		return this;
	}

	public String getKey() {
		return key;
	}

	public RabbitProperties setKey(String key) {
		this.key = key;
		return this;
	}
	
}
