package com.montos.boot.montos.sliding.window.starter.helper;

import com.montos.boot.montos.core.api.exception.ValidatedException;
import com.montos.boot.montos.core.util.AnnotationUtil;
import com.montos.boot.montos.sliding.window.starter.annotation.Publish;
import com.montos.boot.montos.sliding.window.starter.core.IPublishEvent;
import com.montos.boot.montos.sliding.window.starter.core.Publisher;
import com.montos.boot.montos.sliding.window.starter.core.SlidingWindowTemplate;
import com.montos.boot.montos.sliding.window.starter.core.Topic;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PublisherHelper {
	
	private static final Logger log = Logger.getLogger(PublisherHelper.class);
		
	SlidingWindowTemplate slidingWindowTemplate;
	
	String service;
	
	public PublisherHelper setService(String service) {
		this.service = service;
		return this;
	}

	public String getService() {
		return service;
	}

	public PublisherHelper setSlidingWindowTemplate(SlidingWindowTemplate slidingWindowTemplate) {
		this.slidingWindowTemplate = slidingWindowTemplate;
		return this;
	}
	
	public void createPublisher(Method method){
		Publish publish = AnnotationUtil.getAnnotation(method, Publish.class);
		slidingWindowTemplate.createPublisher(new Publisher()
				.setAlias(publish.alias())
				.setKey(PublisherUtil.getPublisherKeyByMethod(method))
				.setService(service));
	}

	
	public void createCounter(Topic topic){
		try{
			log.debug(String.format("create counter[%s] when it do not exist", topic.getKey()));
			slidingWindowTemplate.createCounter(topic);
		}catch(ValidatedException e){
			log.warn(e.getMessage(), e);
		}
		
	}
	
	public void publish(IPublishEvent<?> event){
		slidingWindowTemplate.publish(event);
	}

	public List<Topic> listTopicByPublisher(String publisherKey) {
		List<Topic> list = new ArrayList<Topic>();
		Collection<Topic> topicList = slidingWindowTemplate.listTopic();
		
		for(Topic topic:topicList){
			if(topic.getPublisherKey().trim().length()>0&&topic.getPublisherKey().equals(publisherKey)){
				list.add(topic);
			}
		}
		return list;
	}

}
