package com.montos.boot.montos.sliding.window.starter.helper;

import com.montos.boot.montos.sliding.window.starter.exception.TypeCannotSupportException;

import java.util.HashMap;
import java.util.Map;

public class NumberUtil {
	
	private static final Class<?>[] NUM_CLASSES = new Class[]{Short.class, Float.class, Integer.class, Long.class, Double.class};
	
	private static final Map<Class<?>, Integer> NUM_CLASS_MAP = new HashMap<Class<?>, Integer>();
	
	static{
		for(int i=0;i<NUM_CLASSES.length;i++){
			NUM_CLASS_MAP.put(NUM_CLASSES[i], i);
		}
	}
	
	public static Number add(Number a, Number b){
		if(a==null||a.equals(Double.NaN)||a.equals(0)){
			return b;
		}
		if(b==null||b.equals(Double.NaN)||b.equals(0)){
			return a;
		}

		Class<?> valueType = getIncludeClass(a, b);
		a = parse(a.toString(), valueType);
		b = parse(b.toString(), valueType);
		
		if(a instanceof Double){
			Double c = (Double)a + (Double) b;
			return c;
		}else if(a instanceof Float){
			Float c = (Float)a + (Float) b;
			return c;
		}else if(a instanceof Long){
			Long c = (Long)a + (Long) b;
			return c;
		}else if(a instanceof Integer){
			Integer c = (Integer)a + (Integer) b;
			return c;
		}else if(a instanceof Short){
			Integer c = (Short)a + (Short) b;
			return c;
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compare(Number a, Number b){
		if(a==null||a.equals(Double.NaN)){
			return -1;
		}
		if(b==null||b.equals(Double.NaN)){
			return 1;
		}
		Class<?> valueType = getIncludeClass(a, b);
		a = parse(a.toString(), valueType);
		b = parse(b.toString(), valueType);
		if(a instanceof Comparable){
			return ((Comparable)a).compareTo((Comparable)b);
		}
		return 0;
	}

	public static Number except(Number number, int except){
		if(except==0||number==null||number.equals(Double.NaN)){
			return 0;
		}
		if(number instanceof Double){
			Double c = (Double)number / except;
			return c;
		}else if(number instanceof Float){
			Float c = (Float)number / except;
			return c;
		}else if(number instanceof Long){
			Long c = (Long)number / except;
			return c;
		}else if(number instanceof Integer){
			Integer c = (Integer)number / except;
			return c;
		}else if(number instanceof Short){
			Integer c = (Short)number / except;
			return c;
		}
		return null;
	}
	
	public static void checkType(Class<?> valueType){
		for(Class<?> clazz:NUM_CLASSES){
			if(valueType.equals(clazz)){
				return ;
			}
		}
		throw new TypeCannotSupportException();
	}
	
	private static Class<?> getIncludeClass(Number a, Number b){
		
		if(a==null||b==null){
			return null;
		}
		checkType(a.getClass());
		checkType(b.getClass());
		if(NUM_CLASS_MAP.get(a.getClass())>NUM_CLASS_MAP.get(b.getClass())){
			return a.getClass();
		}else{
			return b.getClass();
		}
	}
	
	
	
	public static Number parse(Object obj, Class<?> valueType){
		if(obj==null){
			return null;
		}
		String number = obj.toString();
		if(number.trim().length()==0){
			return null;
		}
		if(valueType.equals(Double.class)){
			return Double.parseDouble(number);
		}else if(valueType.equals(Float.class)){
			return Float.parseFloat(number);
		}else if(valueType.equals(Long.class)){
			return Long.parseLong(number);
		}else if(valueType.equals(Integer.class)){
			return Integer.parseInt(number);
		}else if(valueType.equals(Short.class)){
			return Short.parseShort(number);
		}
		return null;
	}
}
