package com.montos.boot.montos.sliding.window.starter.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.montos.boot.montos.sliding.window.starter.config.SlidingWindowProperties;
import com.montos.boot.montos.sliding.window.starter.core.ITopicContainer;
import com.montos.boot.montos.sliding.window.starter.core.Topic;
import com.montos.boot.montos.sliding.window.starter.core.TopicContainer;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RedisTopicContainer extends TopicContainer implements ITopicContainer,IRedisSyncTask {
	
	private static final Logger log = Logger.getLogger(RedisPublisherContainer.class);

	SlidingWindowProperties slidingWindowProperties;
	
	@SuppressWarnings("rawtypes")
	RedisTemplate redisTemplate;

	ObjectMapper objectMapper;

	
	public RedisTopicContainer setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	
	public RedisTopicContainer setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	@SuppressWarnings("rawtypes")
	public RedisTopicContainer setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITopicContainer createTopic(Topic topic) {
		log.debug("request create");
		if(!topicMap.containsKey(topic.getKey())&&!topic.isFixed()){
			try {
				redisTemplate.opsForHash().put(slidingWindowProperties.getRedisTopicKey(), topic.getKey(), objectMapper.writeValueAsString(topic));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
		super.createTopic(topic);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITopicContainer deleteTopic(String topic) {
		log.debug("request delete");
		if(topicMap.containsKey(topic)){
			if(!topicMap.get(topic).isFixed()){
				redisTemplate.opsForHash().delete(slidingWindowProperties.getRedisTopicKey(), topic);
			}
		}
		super.deleteTopic(topic);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sync() {
		log.debug("request sync");
		
		Map<String, String> src = redisTemplate.opsForHash().entries(slidingWindowProperties.getRedisTopicKey());
		Map<String, Topic> remoteMap = new HashMap<String, Topic>();
		for(Entry<String,String> entry:src.entrySet()){
			try {
				remoteMap.put(entry.getKey(), objectMapper.readValue(entry.getValue(), Topic.class));
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
		topicMap = remoteMap;
		
	}

}
