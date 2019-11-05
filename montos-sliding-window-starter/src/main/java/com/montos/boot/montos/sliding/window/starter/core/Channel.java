package com.montos.boot.montos.sliding.window.starter.core;

import java.util.List;

public class Channel implements IChannel, Comparable<Channel> {
	
	List<String> topicList;
	
	Long nextTime;
	
	ISubscriber subscriber;

	public Long getNextTime() {
		return nextTime;
	}

	@Override
	public int compareTo(Channel o) {
		if(nextTime>o.getNextTime()){
			return 1;
		}else if(nextTime<o.getNextTime()){
			return -1;
		}
		return 0;
	}

	@Override
	public boolean ready() {
		Long now = System.currentTimeMillis();
		if(now>nextTime){
			Long interval = this.getInterval();
			nextTime = nextTime + interval;
			return true;
		}
 		return false;
	}
	
	/**
	 * 通道执行间隔
	 * @return
	 */
	public Long getInterval(){
		Long interval = subscriber.getInterval();
		if(interval==null||interval<=0){
			interval = subscriber.getLength() * subscriber.getTimeUnit().toMillis(1);
		}
		return interval;
		
	}

	public ISubscriber getSubscriber() {
		return subscriber;
	}

	public Channel setSubscriber(ISubscriber subscriber) {
		this.subscriber = subscriber;
		this.nextTime = this.getStartTime();
		return this;
	}

	public List<String> getTopicList() {
		return topicList;
	}

	public Channel setTopicList(List<String> topicList) {
		this.topicList = topicList;
		return this;
	}
	
	/**
	 * 获取通道的开始时间
	 * @param subscriber
	 * @return
	 */
	protected long getStartTime(){
		if(subscriber.getStart()!=null&&subscriber.getStart()>0){
			return subscriber.getStart();
		}
		long now = System.currentTimeMillis();
		long unitTime = subscriber.getTimeUnit().toMillis(1);
		
		long time = now - now % unitTime;
		while(time < now){
			time = time + this.getInterval();
		}
		
		return time;
	}
	
}
