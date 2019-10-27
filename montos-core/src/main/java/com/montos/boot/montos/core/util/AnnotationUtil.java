package com.jimistore.boot.nemo.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationUtil {
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getAnnotation(Class classz,Class<T> annotation){
		Annotation[] cas=classz.getAnnotations();
		for(Annotation ca:cas){
			if(ca.annotationType().equals(annotation)){
				return (T)ca;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getAnnotation(Field classz,Class<T> annotation){
		Annotation[] cas=classz.getAnnotations();
		for(Annotation ca:cas){
			if(ca.annotationType().equals(annotation)){
				return (T)ca;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getAnnotation(Method classz,Class<T> annotation){
		Annotation[] cas=classz.getAnnotations();
		for(Annotation ca:cas){
			if(ca.annotationType().equals(annotation)){
				return (T)ca;
			}
		}
		return null;
	}
}
