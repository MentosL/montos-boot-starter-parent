package com.montos.boot.montos.sliding.window.starter.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeUnitParser {
	
	static final Map<String, TimeUnit> map = new HashMap<String, TimeUnit>();
	
	static final Map<TimeUnit, String> reverseMap = new HashMap<TimeUnit, String>();
	
	static{
		for(TimeUnit timeUnit:TimeUnit.values()){
			map.put(timeUnit.toString(), timeUnit);
			reverseMap.put(timeUnit, timeUnit.toString());
		}
	}
	
	public static String parse(TimeUnit timeUnit){
		return reverseMap.get(timeUnit);
	}
	
	public static TimeUnit parse(String timeUnit){
		return map.get(timeUnit);
	}

}
