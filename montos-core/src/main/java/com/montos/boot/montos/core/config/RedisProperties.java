package com.jimistore.boot.nemo.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nemo.redis")
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
