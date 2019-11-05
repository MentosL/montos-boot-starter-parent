package com.montos.boot.montos.sliding.window.starter.helper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

public class SpelUtil {

	/**
	 * spel格式化
	 * @param context
	 * @param str
	 * @return
	 */
	public static <T> T parseExpression(StandardEvaluationContext context,String str, Class<T> clazz){
		return new SpelExpressionParser().parseExpression(str).getValue(context, clazz);
	}
	
	/**
	 * 获取函数spel对应初始化的上下文
	 * @param joinPoint
	 * @return
	 */
	public static StandardEvaluationContext getContextByProceedingJoinPoint(ProceedingJoinPoint joinPoint, Object result, Throwable error){
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		Object[] objs = joinPoint.getArgs();
		StandardEvaluationContext context = new StandardEvaluationContext();
		ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
		for(int i=0;i<objs.length;i++){
			context.setVariable(String.format("%s%s", "p", i), objs[i]);
		}
		for(int i=0;i<objs.length;i++){
			context.setVariable(parameterNames[i], objs[i]);
		}
		context.setVariable("result", result);
		context.setVariable("error", error);

		return context;
	}
	
}
