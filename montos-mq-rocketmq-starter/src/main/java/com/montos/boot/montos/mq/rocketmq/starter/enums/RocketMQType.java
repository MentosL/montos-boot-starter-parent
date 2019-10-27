package com.montos.boot.montos.mq.rocketmq.starter.enums;

import java.util.HashMap;
import java.util.Map;

public enum RocketMQType {
	
	NORMAL("normal", "无序消息"),
	ORDER("order", "顺序消息");
	
	String code;
	
	String alias;
	
	static Map<String, RocketMQType> map = new HashMap<String, RocketMQType>();
	
	static {
		for(RocketMQType type : RocketMQType.values()){
			map.put(type.getCode(), type);
		}
	}

	private RocketMQType(String code, String alias) {
		this.code = code;
		this.alias = alias;
	}

	public String getCode() {
		return code;
	}

	public String getAlias() {
		return alias;
	}
	
	public static RocketMQType parse(String code){
		return map.get(code);
	}
	

}
