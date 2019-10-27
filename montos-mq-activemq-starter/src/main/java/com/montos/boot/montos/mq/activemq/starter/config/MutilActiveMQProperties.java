package com.montos.boot.montos.mq.activemq.starter.config;

import com.montos.boot.montos.mq.activemq.starter.adapter.MyActiveMQProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("montos.mq")
public class MutilActiveMQProperties {
	
	public static final String MQ_DATASOURCE_TYPE_ACTIVEMQ = "activemq";
	
	Map<String, MyActiveMQProperties> activemq = new HashMap<String, MyActiveMQProperties>();

	public Map<String, MyActiveMQProperties> getActivemq() {
		return activemq;
	}

	public MutilActiveMQProperties setActivemq(Map<String, MyActiveMQProperties> activemq) {
		this.activemq = activemq;
		return this;
	}
	
}
