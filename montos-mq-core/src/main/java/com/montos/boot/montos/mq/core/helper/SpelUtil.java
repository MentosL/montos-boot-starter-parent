package com.montos.boot.montos.mq.core.helper;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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
	public static StandardEvaluationContext getContextByProceedingJoinPoint(MethodInvocation invocation){
		Object[] objs = invocation.getArguments();
		StandardEvaluationContext context = new StandardEvaluationContext();
		for(int i=0;i<objs.length;i++){
			context.setVariable(String.format("%s%s", "p", i), objs[i]);
		}

		return context;
	}
	
}
