package com.montos.boot.montos.sliding.window.starter.helper;

import java.lang.reflect.Method;

public class PublisherUtil {
	
	public static String getPublisherKeyByMethod(Method method){
		StringBuilder param = new StringBuilder();
		for(Class<?> type:method.getParameterTypes()){
			if(param.length()>0){
				param.append(",");
			}
			param.append(type.getSimpleName());
		}
		return String.format("%s-%s-%s", 
				method.getDeclaringClass().getName(), 
				method.getName(),
				param.toString());
	}

}
