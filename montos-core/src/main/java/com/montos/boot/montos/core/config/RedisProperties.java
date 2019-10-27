package com.montos.boot.montos.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("montos.redis")
public class RedisProperties {
	
	Map<String,Long> expired = new HashMap<String,Long>();

	public Map<String, Long> getExpired() {
		return expired;
	}

	public RedisProperties setExpired(Map<String, Long> expired) {
		this.expired = expired;
		return this;
	}
	
	

}
