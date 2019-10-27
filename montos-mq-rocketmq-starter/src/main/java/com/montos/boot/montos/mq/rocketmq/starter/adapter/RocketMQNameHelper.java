package com.montos.boot.montos.mq.rocketmq.starter.adapter;

import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.mq.core.api.annotation.JsonMQService;
import com.montos.boot.montos.mq.core.helper.MQNameHelper;
import com.montos.boot.montos.mq.rocketmq.starter.config.MutilRocketMQProperties;

import java.lang.reflect.Method;

public class RocketMQNameHelper extends MQNameHelper {
	
	MutilRocketMQProperties mutilRocketMQProperties;
	
	public RocketMQNameHelper setMutilRocketMQProperties(MutilRocketMQProperties mutilRocketMQProperties) {
		this.mutilRocketMQProperties = mutilRocketMQProperties;
		return this;
	}


	@Override
	protected String getDestinationName(Class<?> clazz, Method method) {
		String destName = super.getDestinationName(clazz, method);
		if(destName!=null){
			if(clazz.isInterface()){
				if(clazz.isAnnotationPresent(JsonMQService.class)){
					String alias = this.getAliasByClass(clazz, destName);
					if(alias!=null){
						return alias;
					}
				}
				
			}else{
				for(Class<?> intf:clazz.getInterfaces()){
					if(intf.isAnnotationPresent(JsonMQService.class)){
						String alias = this.getAliasByClass(intf, destName);
						if(alias!=null){
							return alias;
						}
					}
					
				}
				
			}
		}
		
		return destName;
	}
	
	private String getAliasByClass(Class<?> clazz, String destName){
		JsonMQService jsonMQService = AnnotationUtil.getAnnotation(clazz, JsonMQService.class);
		RocketMQProperties rocketMQProperties = mutilRocketMQProperties.getRocketmq().get(jsonMQService.value());
		if(rocketMQProperties!=null){
			String alias = rocketMQProperties.getTopicMap().get(destName);
			if(alias!=null){
				return alias;
			}
		}
		return null;
	}

}
