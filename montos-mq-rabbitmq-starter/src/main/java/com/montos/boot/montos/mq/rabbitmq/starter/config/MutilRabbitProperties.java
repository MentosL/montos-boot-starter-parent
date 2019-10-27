package com.montos.boot.montos.mq.rabbitmq.starter.config;

import com.montos.boot.montos.mq.rabbitmq.starter.adapter.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties("montos.mq")
public class MutilRabbitProperties {
	
	Map<String, RabbitProperties> rabbitmq = new HashMap<String, RabbitProperties>();

	public Map<String, RabbitProperties> getRabbitmq() {
		return rabbitmq;
	}

	public MutilRabbitProperties setRabbitmq(Map<String, RabbitProperties> rabbitmq) {
		this.rabbitmq = rabbitmq;
		return this;
	}

}
