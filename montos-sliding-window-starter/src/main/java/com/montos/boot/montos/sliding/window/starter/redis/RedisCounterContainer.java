package com.montos.boot.montos.sliding.window.starter.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.montos.boot.montos.core.api.exception.ValidatedException;
import com.montos.boot.montos.sliding.window.starter.config.SlidingWindowProperties;
import com.montos.boot.montos.sliding.window.starter.core.ICounter;
import com.montos.boot.montos.sliding.window.starter.core.ICounterContainer;
import com.montos.boot.montos.sliding.window.starter.core.LocalCounterContainer;
import com.montos.boot.montos.sliding.window.starter.core.Topic;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * 处理计数器容器的同步
 * 
 * @author chenqi
 * @Date 2018年7月17日
 *
 */
public class RedisCounterContainer extends LocalCounterContainer implements IRedisSyncTask {
	
	private static final Logger log = Logger.getLogger(RedisCounterContainer.class);

	@SuppressWarnings("rawtypes")
	private RedisTemplate redisTemplate;

	private SlidingWindowProperties slidingWindowProperties;

	private ObjectMapper objectMapper;
	
	Map<String, CounterMsg> remoteMap = new HashMap<String, CounterMsg>();

	public RedisCounterContainer() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public RedisCounterContainer setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	public RedisCounterContainer setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	public RedisCounterContainer setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ICounterContainer createCounter(Topic topic) {
		if(log.isDebugEnabled()){
			log.debug(String.format("create redis counter[%s]", topic.getKey()));
		}
		if(counterMap.containsKey(topic.getKey())){
			throw new ValidatedException(String.format("counter[%s] is exist", topic.getKey()));
		}
		RedisCounter<?> counter = RedisCounter.create(slidingWindowProperties, 
				redisTemplate, 
				topic.getKey(), 
				topic.getTimeUnit(), 
				topic.getCapacity(),
				topic.getValueType());
		counterMap.put(topic.getKey(), counter);
		if(log.isDebugEnabled()){
			log.debug(String.format("created redis counter[%s]", topic.getKey()));
		}

		// 同步给redis
		CounterMsg counterMsg = new CounterMsg().setCapacity(topic.getCapacity()).setKey(topic.getKey()).setClassName(topic.getClassName())
				.setTimeUnit(topic.getTimeUnitStr());

		try {
			boolean result = redisTemplate.opsForHash().putIfAbsent(slidingWindowProperties.getRedisContainerKey(), topic.getKey(),
					objectMapper.writeValueAsString(counterMsg));
			if(result){
				redisTemplate.expire(slidingWindowProperties.getRedisContainerKey(), slidingWindowProperties.getRedisExpired(),
						TimeUnit.MILLISECONDS);
			}else{
				counter.sync();
			}
			if(log.isDebugEnabled()){
				log.debug(String.format("uploaded redis counter[%s]", topic.getKey()));
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ICounterContainer deleteCounter(String key) {
		if(log.isDebugEnabled()){
			log.debug(String.format("delete counter[%s]", key));
		}
		if(counterMap.containsKey(key)){
			redisTemplate.opsForHash().delete(slidingWindowProperties.getRedisContainerKey(), key);
		}
		super.deleteCounter(key);
		return this;
	}

	protected List<CounterMsg> getNotExistCounterList(){
		List<CounterMsg> counterMsgList = new ArrayList<CounterMsg>();
		for (String key : remoteMap.keySet()) {
			if (!counterMap.containsKey(key)) {
				try {
					CounterMsg counter = remoteMap.get(key);
					counterMsgList.add(counter);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return counterMsgList;
		
	}
	
	protected List<ICounter<?>> getOverflowCounterList(){
		List<ICounter<?>> counterMsgList = new ArrayList<ICounter<?>>();
		
		for (String key : counterMap.keySet()) {
			if (!remoteMap.containsKey(key)) {
				counterMsgList.add(counterMap.get(key));
			}
		}
		return counterMsgList;
		
	}

	@SuppressWarnings("unchecked")
	public void sync() {
		// 同步计数容器
		Map<String, String> src = redisTemplate.opsForHash()
				.entries(slidingWindowProperties.getRedisContainerKey());
		Map<String, CounterMsg> temp = new HashMap<String, CounterMsg>();
		for (String key : src.keySet()) {
			try {
				String content = src.get(key);
				CounterMsg counter = objectMapper.readValue(content, CounterMsg.class);
				temp.put(key, counter);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		remoteMap = temp;
		
		// 同步计数
		for (Entry<String, ICounter<?>> entry : counterMap.entrySet()) {
			RedisCounter<?> counter = (RedisCounter<?>) entry.getValue();
			counter.sync();
		}
	}

}
