package com.montos.boot.montos.sliding.window.starter.helper;

import com.jimistore.boot.nemo.sliding.window.annotation.Publish;
import com.jimistore.boot.nemo.sliding.window.annotation.Publisher;
import com.jimistore.boot.nemo.sliding.window.annotation.Subscribe;
import com.jimistore.boot.nemo.sliding.window.annotation.Subscriber;
import com.jimistore.util.reflex.AnnotationUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SlidingWindowClient implements BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {
	
	private static final Logger log = Logger.getLogger(SlidingWindowClient.class);
	
	ApplicationContext applicationContext;
	
	ConfigurableListableBeanFactory beanFactory;
	
	PublisherHelper publisherHelper;
	
	SubscriberHelper subscriberHelper;
	
	Map<String, Subscriber> subscriberMap = new HashMap<String, Subscriber>();
	Map<String, Publisher> publisherMap = new HashMap<String, Publisher>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		
	}

	public SlidingWindowClient setPublisherHelper(PublisherHelper publisherHelper) {
		this.publisherHelper = publisherHelper;
		return this;
	}

	public SlidingWindowClient setSubscriberHelper(SubscriberHelper subscriberHelper) {
		this.subscriberHelper = subscriberHelper;
		return this;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
        	
        	Subscriber subscriber = beanFactory.findAnnotationOnBean(beanName, Subscriber.class);
        	if(subscriber!=null){
        		subscriberMap.put(beanName, subscriber);
        	}
        	
        	Publisher publisher = beanFactory.findAnnotationOnBean(beanName, Publisher.class);
        	if(publisher!=null){
        		publisherMap.put(beanName, publisher);
        	}
            
        }
	}
    private <T> Map<Method, T> parseAnnotationList(Class<?> clazz, Class<T> annotation){
    	Map<Method, T> map = new HashMap<Method, T>();
    	for(Method method:clazz.getDeclaredMethods()){
    		T t = AnnotationUtil.getAnnotation(method, annotation);
    		if(t!=null){
    			map.put(method, t);
    		}
    	}
    	return map;
    }

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = null;
    	
    	//处理发布注解
    	Publisher publisher = publisherMap.get(beanName);
    	if(publisher!=null){
    		if(clazz==null){
    			clazz = this.getClass(beanName);
    		}
    		Map<Method, Publish> methodMap = this.parseAnnotationList(clazz, Publish.class);
			for(Map.Entry<Method, Publish> entry:methodMap.entrySet()){
				if(log.isDebugEnabled()){
					log.debug(String.format("create publisher[%s]", entry.getKey().getName()));
				}
				try{
					publisherHelper.createPublisher(entry.getKey());
				}catch(Exception e){
					log.warn(e.getMessage(), e);
				}
        	}
    	}
    	
    	//处理订阅注解
		Subscriber subscriber = subscriberMap.get(beanName);
    	if(subscriber!=null){
    		if(clazz==null){
    			clazz = this.getClass(beanName);
    		}
			Map<Method, Subscribe> methodMap = this.parseAnnotationList(clazz, Subscribe.class);
			for(Map.Entry<Method, Subscribe> entry:methodMap.entrySet()){
				if(log.isDebugEnabled()){
					log.debug(String.format("create subscriber[%s:%s]", entry.getValue(), entry.getKey().getName()));
				}
				subscriberHelper.createSubscriber(entry.getValue(), entry.getKey(), bean);
			}
    	}
		return bean;
	}
    
	
	/**
	 * Find a {@link BeanDefinition} in the {@link BeanFactory} or it's parents.
	 */
	private BeanDefinition findBeanDefintion(
		ConfigurableListableBeanFactory beanFactory, String serviceBeanName) {
		if (beanFactory.containsLocalBean(serviceBeanName)) {
			return beanFactory.getBeanDefinition(serviceBeanName);
		}
		BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
		if (parentBeanFactory != null
			&& ConfigurableListableBeanFactory.class.isInstance(parentBeanFactory)) {
			return findBeanDefintion(
				(ConfigurableListableBeanFactory) parentBeanFactory,
				serviceBeanName);
		}
		throw new RuntimeException(String.format(
				"Bean with name '%s' can no longer be found.", serviceBeanName));
	}
	
	private Class<?> getClass(String serviceBeanName){
		BeanDefinition beanDefinition = this.findBeanDefintion(beanFactory, serviceBeanName);
 		String className = beanDefinition.getBeanClassName();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
