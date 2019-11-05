package com.montos.boot.montos.sliding.window.starter.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TopicContainer implements ITopicContainer {
	
	protected Map<String, Topic> topicMap = new HashMap<String, Topic>();
	
	@Override
	public Collection<Topic> listTopic() {
		return topicMap.values();
	}

	@Override
	public ITopicContainer createTopic(Topic topic) {
		if(topicMap.containsKey(topic.getKey())){
			throw new RuntimeException(String.format("topic[%s] exist", topic.getKey()));
		}
		topicMap.put(topic.getKey(), topic);
		return this;
	}

	@Override
	public ITopicContainer deleteTopic(String topic) {
		if(!topicMap.containsKey(topic)){
			throw new RuntimeException(String.format("topic[%s] exist", topic));
		}
		topicMap.remove(topic);
		return this;
	}

	@Override
	public Topic getTopic(String topicKey) {
		return topicMap.get(topicKey);
	}

}
