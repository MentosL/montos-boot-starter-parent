package com.montos.boot.montos.sliding.window.starter.core;

import com.jimistore.boot.nemo.core.api.exception.ValidatedException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * 计数容器
 * @author chenqi
 * @Date 2018年6月1日
 *
 */
public class LocalCounterContainer implements ICounterContainer {
	
	private static final Logger log = Logger.getLogger(LocalCounterContainer.class);
	
	protected Map<String, ICounter<?>> counterMap = new HashMap<String, ICounter<?>>();
		
	public LocalCounterContainer(){
	}

	@Override
	public ICounterContainer publish(IPublishEvent<?> event) {
		if(log.isDebugEnabled()){
			log.debug(String.format("request put[%s]", event.getTopicKey()));
		}
		if(event==null){
			throw new ValidatedException("event can not be null");
		}
		ICounter<?> counter = getCounterByKey(event.getTopicKey());
		counter.put(event);
		return this;
	}

	@Override
	public ICounterContainer createCounter(Topic topic) {
		if(log.isDebugEnabled()){
			log.debug(String.format("create counter[%s]", topic.getKey()));
		}

		if(counterMap.containsKey(topic.getKey())){
			throw new ValidatedException(String.format("counter[%s] is exist", topic.getKey()));
		}
		ICounter<?> counter = Counter.create(topic.getKey(), topic.getTimeUnit(), topic.getCapacity(), topic.getValueType());
		counterMap.put(topic.getKey(), counter);
		
		return this;
	}

	@Override
	public ICounterContainer deleteCounter(String key) {
		if(log.isDebugEnabled()){
			log.debug(String.format("delete counter[%s]", key));
		}
		counterMap.remove(key);
		return this;
	}
	
	protected ICounter<?> getCounterByKey(String key){
		if(key==null){
			throw new ValidatedException("key of event can not be null");
		}
		
		ICounter<?> counter = counterMap.get(key);
		if(counter==null){
			throw new ValidatedException(String.format("can not find counter[%s]", key));
		}
		return counter;
	}
	
	public void heartbeat(){
		for(Entry<String, ICounter<?>> entry:counterMap.entrySet()){
			entry.getValue().heartbeat();
		}
	}

	@Override
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType) {
		ICounter<?> counter = this.getCounterByKey(key);
		return counter.window(timeUnit, length, valueType);
	}

	@Override
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType, long timestamp) {
		ICounter<?> counter = this.getCounterByKey(key);
		return counter.window(timeUnit, length, valueType, timestamp);
	}
	
}
