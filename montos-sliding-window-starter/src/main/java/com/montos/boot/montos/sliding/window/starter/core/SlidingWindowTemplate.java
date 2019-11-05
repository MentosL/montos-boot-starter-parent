package com.montos.boot.montos.sliding.window.starter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.montos.boot.montos.sliding.window.starter.config.SlidingWindowProperties;
import com.montos.boot.montos.sliding.window.starter.exception.ConfigException;
import com.montos.boot.montos.sliding.window.starter.handler.INoticeHandler;
import com.montos.boot.montos.sliding.window.starter.handler.IPublishHandler;
import com.montos.boot.montos.sliding.window.starter.redis.RedisCounterContainer;
import com.montos.boot.montos.sliding.window.starter.redis.RedisDispatcher;
import com.montos.boot.montos.sliding.window.starter.redis.RedisPublisherContainer;
import com.montos.boot.montos.sliding.window.starter.redis.RedisTopicContainer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 调度中心代理类-主入口
 * @author chenqi
 * @Date 2018年6月6日
 *
 */
public class SlidingWindowTemplate implements IDispatcher, IPublisherContainer, ITopicContainer, ICounterContainer {
	
	SlidingWindowProperties slidingWindowProperties;
	
	IDispatcher dispatcher;
	
	IPublisherContainer publisherContainer;
	
	ITopicContainer topicContainer;
	
	ICounterContainer counterContainer;
	
	private SlidingWindowTemplate(){
		
	}
	
	private SlidingWindowTemplate setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	public SlidingWindowProperties getSlidingWindowProperties() {
		return slidingWindowProperties;
	}

	private SlidingWindowTemplate setDispatcher(IDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		return this;
	}
	
	private SlidingWindowTemplate setPublisherContainer(IPublisherContainer publisherContainer) {
		this.publisherContainer = publisherContainer;
		return this;
	}

	private SlidingWindowTemplate setTopicContainer(ITopicContainer topicContainer) {
		this.topicContainer = topicContainer;
		return this;
	}

	private SlidingWindowTemplate setCounterContainer(ICounterContainer counterContainer) {
		this.counterContainer = counterContainer;
		return this;
	}

	public static final SlidingWindowTemplate create(SlidingWindowProperties slidingWindowProperties){
		return create(slidingWindowProperties, null, null);
	}

	public static final SlidingWindowTemplate create(SlidingWindowProperties slidingWindowProperties, RedisTemplate<?,?> redisTemplate, ObjectMapper objectMapper){
		if(slidingWindowProperties==null){
			throw new ConfigException("sliding window properties connot be null");
		}
		Dispatcher dispatcher = null;
		IPublisherContainer publisherContainer = null;
		ITopicContainer topicContainer = null;
		ICounterContainer counterContainer = null;
		
		if(!StringUtils.isEmpty(slidingWindowProperties.getCacheModel())&&slidingWindowProperties.getCacheModel().equals(SlidingWindowProperties.CACHE_MODEL_REDIS)){
			dispatcher = new RedisDispatcher();
			counterContainer = new RedisCounterContainer()
					.setObjectMapper(objectMapper)
					.setRedisTemplate(redisTemplate)
					.setSlidingWindowProperties(slidingWindowProperties);
			publisherContainer = new RedisPublisherContainer()
					.setObjectMapper(objectMapper)
					.setRedisTemplate(redisTemplate)
					.setSlidingWindowProperties(slidingWindowProperties);
			topicContainer = new RedisTopicContainer()
					.setObjectMapper(objectMapper)
					.setRedisTemplate(redisTemplate)
					.setSlidingWindowProperties(slidingWindowProperties);
		}else{
			publisherContainer = new PublisherContainer();
			counterContainer = new LocalCounterContainer();
			topicContainer = new TopicContainer();
			dispatcher = new Dispatcher();
		}
		return new SlidingWindowTemplate()
				.setSlidingWindowProperties(slidingWindowProperties)
				.setDispatcher(dispatcher
						.setCounterContainer(counterContainer)
						.setChannelContainer(new ChannelContainer())
						.setPublisherContainer(publisherContainer)
						.setTopicContainer(topicContainer)
						.setSlidingWindowProperties(slidingWindowProperties)
						.init())
				.setTopicContainer(topicContainer)
				.setCounterContainer(counterContainer)
				.setPublisherContainer(publisherContainer);
		
	}

	@Override
	public SlidingWindowTemplate subscribe(ISubscriber subscriber) {
		dispatcher.subscribe(subscriber);
		return this;
		
	}

	@Override
	public SlidingWindowTemplate publish(IPublishEvent<?> event) {
		dispatcher.publish(event);
		return this;
	}

	@Override
	public SlidingWindowTemplate addPublishHandler(IPublishHandler publishHandler) {
		dispatcher.addPublishHandler(publishHandler);
		return this;
	}

	@Override
	public SlidingWindowTemplate addNoticeHandler(INoticeHandler noticeHandler) {
		dispatcher.addNoticeHandler(noticeHandler);
		return this;
	}

	@Override
	public SlidingWindowTemplate createCounter(Topic topic) {
		dispatcher.createCounter(topic);
		return this;
	}

	@Override
	public Collection<Topic> listTopic() {
		return topicContainer.listTopic();
	}

	@Override
	public SlidingWindowTemplate createTopic(Topic topic) {
		dispatcher.createTopic(topic);
		return this;
	}
	
	@Override
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType) {
		return counterContainer.window(key, timeUnit, length, valueType);
	}

	@Override
	public Collection<Publisher> listPublisher() {
		return publisherContainer.listPublisher();
	}

	@Override
	public SlidingWindowTemplate createPublisher(Publisher publisher) {
		publisherContainer.createPublisher(publisher);
		return this;
	}

	@Override
	public SlidingWindowTemplate deletePublisher(String publisherKey) {
		publisherContainer.deletePublisher(publisherKey);
		return this;
	}

	@Override
	public SlidingWindowTemplate deleteTopic(String topicKey) {
		dispatcher.deleteTopic(topicKey);
		return this;
	}

	@Override
	public SlidingWindowTemplate deleteCounter(String key) {
		dispatcher.deleteCounter(key);
		return this;
	}

	@Override
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType, long timestamp) {
		return counterContainer.window(key, timeUnit, length, valueType, timestamp);
	}

	@Override
	public Topic getTopic(String topicKey) {
		return topicContainer.getTopic(topicKey);
	}

	@Override
	public IDispatcher unsubscribe(ISubscriber subscriber) {
		dispatcher.unsubscribe(subscriber);
		return this;
	}
}
