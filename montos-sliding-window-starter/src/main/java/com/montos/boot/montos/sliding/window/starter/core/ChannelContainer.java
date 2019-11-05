package com.montos.boot.montos.sliding.window.starter.core;

import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;

import java.util.*;

public class ChannelContainer implements IChannelContainer {
	
//	Map<String, List<IChannel>> channelMap = new HashMap<String, List<IChannel>>();
	
	Set<String> topicList = new HashSet<String>();

	List<Channel> channelList = new ArrayList<Channel>();
	
	Set<ISubscriber> subscriberSet = new HashSet<ISubscriber>();
	
	AntPathMatcher matcher = new AntPathMatcher();
	
	private static final Logger log = Logger.getLogger(ChannelContainer.class);

	@Override
	public ChannelContainer put(ISubscriber subscriber) {
		if(!subscriberSet.contains(subscriber)){
			subscriberSet.add(subscriber);
			//如果是逻辑主题，不需要匹配是否有topic
			if(subscriber instanceof ILogicSubscriber){
				ILogicSubscriber logicSubscriber = (ILogicSubscriber)subscriber;
				
				List<String> topicKeyList = new ArrayList<String>();
				topicKeyList.addAll(logicSubscriber.getTopicVariableMap().keySet());
				channelList.add(new Channel()
						.setSubscriber(subscriber)
						.setTopicList(topicKeyList));
			}else{
				for(String key:topicList){
					if(!this.match(key, subscriber.getTopicMatch())){
						continue;
					}
					
					List<String> topicKeyList = new ArrayList<String>();
					topicKeyList.add(key);
					channelList.add(new Channel()
							.setSubscriber(subscriber)
							.setTopicList(topicKeyList));
				
				}
			}
		}
		return this;
	}

	@Override
	public IChannelContainer put(String topic) {
		synchronized (topicList) {
			if(topicList.contains(topic)){
				return this;
			}
			topicList.add(topic);
			for(ISubscriber subscriber:subscriberSet){
				if(log.isDebugEnabled()){
					log.debug(String.format("match subscriber %s:%s", subscriber.getTopicMatch(), topic));
				}
				if(this.match(topic, subscriber.getTopicMatch())){
					List<String> topicKeyList = new ArrayList<String>();
					topicKeyList.add(topic);
					Channel channel = new Channel()
					.setSubscriber(subscriber)
					.setTopicList(topicKeyList);
					channelList.add(channel);
					
					if(log.isDebugEnabled()){
						log.debug(String.format("create channel %s:%s , channelList:%s", subscriber.getTopicMatch(), topic, channelList));
					}
				}
			}
			
		}
		
		return this;
	}
	
	protected boolean match(String key, String topicMatch){
		return matcher.match(topicMatch, key);
	}

	@Override
	public IChannelContainer delete(String key) {
		synchronized (channelList) {
			Iterator<Channel> it = channelList.iterator();
			while(it.hasNext()){
				Channel channel = it.next();
				if(channel.getTopicList().contains(key)){
					it.remove();
					topicList.remove(key);
				}
			}
		}
		return this;
	}
	
	@Override
	public IChannelContainer delete(ISubscriber subscriber) {
		synchronized (channelList) {
			Iterator<Channel> it = channelList.iterator();
			while(it.hasNext()){
				Channel channel = it.next();
				if(channel.getSubscriber().equals(subscriber)){
					it.remove();
				}
			}
		}
		return this;
	}
	
	@Override
	public List<Channel> listAllChannel() {
		return channelList;
	}

}
