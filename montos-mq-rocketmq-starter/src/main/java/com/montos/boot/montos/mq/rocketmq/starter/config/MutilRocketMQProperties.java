package com.montos.boot.montos.mq.rocketmq.starter.config;

import com.montos.boot.montos.mq.rocketmq.starter.adapter.RocketMQProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("montos.mq")
public class MutilRocketMQProperties {
	
	public static final String ROCKETMQ  = "rocketmq";

	Map<String, RocketMQProperties> rocketmq = new HashMap<String, RocketMQProperties>();

	public Map<String, RocketMQProperties> getRocketmq() {
		return rocketmq;
	}

	public MutilRocketMQProperties setRocketmq(Map<String, RocketMQProperties> rocketmq) {
		this.rocketmq = rocketmq;
		return this;
	}
	
}
