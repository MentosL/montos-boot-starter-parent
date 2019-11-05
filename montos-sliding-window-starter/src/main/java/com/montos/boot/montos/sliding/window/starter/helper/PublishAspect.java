package com.montos.boot.montos.sliding.window.starter.helper;


import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.sliding.window.starter.annotation.Publish;
import com.montos.boot.montos.sliding.window.starter.core.PublishEvent;
import com.montos.boot.montos.sliding.window.starter.core.Topic;
import com.montos.boot.montos.sliding.window.starter.exception.SpelParseException;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Order(15)
public class PublishAspect {
	
	private static final Logger log = Logger.getLogger(PublishAspect.class);
	
	private PublisherHelper publisherHelper;

	public PublishAspect setPublisherHelper(PublisherHelper publisherHelper) {
		this.publisherHelper = publisherHelper;
		return this;
	}


	@Pointcut("@annotation(com.montos.boot.montos.sliding.window.starter.annotation.Publish)")
	public void publish(){
	}
	
	@Around("publish()")
	public Object aroundProduce(ProceedingJoinPoint joinPoint) throws Throwable{
		Throwable throwable = null;
		Object result = null;
		try{
			Signature signature = joinPoint.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			long cost = 0;
			try{
				long old = System.currentTimeMillis();
				result=joinPoint.proceed(joinPoint.getArgs());
				cost = System.currentTimeMillis() - old;
			}catch(Throwable e){
				throwable = e;
			}
			List<Topic> topicList = this.getTopicList(method);
			
			for(Topic topic:topicList){
				
				try{
					StandardEvaluationContext context = this.getContextByProceedingJoinPoint(joinPoint, result, throwable, cost);
					String keyEl = topic.getKey();
					if(keyEl==null){
						log.warn("topic's key cannot be null");
						continue ;
					}
					if(keyEl.indexOf("\"")!=0&&keyEl.indexOf("'")!=0){
						keyEl = String.format("\"%s\"", keyEl);
					}
					String key = this.parseExpression(context, keyEl, String.class);
					if(key.indexOf("\"")==0&&key.lastIndexOf("\"")==key.length()-1){
						key=key.substring(0, key.length()-1);
					}
					boolean condition = this.parseExpression(context, topic.getCondition(), Boolean.class);
					int num = this.parseExpression(context, topic.getNum(), Integer.class);
					
					if(log.isDebugEnabled()){
						log.debug(String.format("check topic[%s], the condition is %s=%s, the result is %s, the error is %s", key, topic.getCondition(), condition, result, throwable));
					}
					publisherHelper.createCounter(topic.setKey(key));
					if(condition){
						if(log.isDebugEnabled()){
							log.debug(String.format("publish counter %s", key));
						}
						publisherHelper.publish(new PublishEvent<Integer>()
								.setTime(System.currentTimeMillis())
								.setTopicKey(key)
								.setValue(num)
								);
					}
				}catch(SpelParseException e){
					log.error(e);
					continue ;
				}
			}
		}catch(Throwable t){
			log.warn(t.getMessage(), t);
		}
			
		if(throwable!=null){
			throw throwable;
		}
		
		return result;
	}
	
	
	/**
	 * spel格式化
	 * @param context
	 * @param str
	 * @return
	 */
	private <T> T parseExpression(StandardEvaluationContext context,String str, Class<T> clazz){
		try{
			return new SpelExpressionParser().parseExpression(str).getValue(context, clazz);
		}catch(Exception e){
			log.error(String.format("spel parse error, the expression is '%s'", str));
			throw new SpelParseException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取函数spel对应初始化的上下文
	 * @param joinPoint
	 * @return
	 */
	private StandardEvaluationContext getContextByProceedingJoinPoint(ProceedingJoinPoint joinPoint, Object result, Throwable error, long cost){
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
		context.setVariable("cost", cost);

		return context;
	}
	
	/**
	 * 
	 * @return
	 */
	protected List<Topic> getTopicList(Method method){
		List<Topic> topicList = new ArrayList<Topic>();
		Publish publish = AnnotationUtil.getAnnotation(method, Publish.class);
		for(Topic topic:publish.value()){
			topicList.add(Topic.from(topic));
		}
		
		if(publisherHelper!=null){
			topicList.addAll(publisherHelper.listTopicByPublisher(PublisherUtil.getPublisherKeyByMethod(method)));
		}
		return topicList;
	}
	
	
	
}

