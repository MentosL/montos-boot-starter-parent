package com.montos.boot.montos.sliding.window.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * sliding-window配置
 * @author chenqi
 * @Date 2018年7月17日
 *
 */
@ConfigurationProperties("nemo.swindow")
public class SlidingWindowProperties {
	
	public static final String CACHE_MODEL_LOCAL = "local";
	
	public static final String CACHE_MODEL_REDIS = "redis";
		
	String cacheModel = CACHE_MODEL_LOCAL;
	
	/**
	 * 同步间隔(毫秒)
	 */
	long syncInterval = 30000;
	
	/**
	 * 失效清除计数器的计数容量
	 */
	int expiredCapacity = 60;
	
	/**
	 * 失效清除计数器倒数的偏移量
	 */
	int expiredOffset = 60;
	
	/**
	 * redis key的时效时间(毫秒)
	 */
	long redisExpired = 365 * 24 * 60 * 60 * 1000;
	
	/**
	 * redis 容器的key
	 */
	String redisContainerKey = "nemo-sliding-window-container";
	
	String redisPublisherKey = "nemo-sliding-window-publisher";
	
	String redisTopicKey = "nemo-sliding-window-topic";
	
	/**
	 * 通知线程数大小
	 */
	int maxNoticeThreadSize=10;

	public String getCacheModel() {
		return cacheModel;
	}

	public SlidingWindowProperties setCacheModel(String cacheModel) {
		this.cacheModel = cacheModel;
		return this;
	}

	public long getSyncInterval() {
		return syncInterval;
	}

	public SlidingWindowProperties setSyncInterval(long syncInterval) {
		this.syncInterval = syncInterval;
		return this;
	}

	public int getExpiredCapacity() {
		return expiredCapacity;
	}

	public SlidingWindowProperties setExpiredCapacity(int expiredCapacity) {
		this.expiredCapacity = expiredCapacity;
		return this;
	}

	public int getExpiredOffset() {
		return expiredOffset;
	}

	public SlidingWindowProperties setExpiredOffset(int expiredOffset) {
		this.expiredOffset = expiredOffset;
		return this;
	}

	public long getRedisExpired() {
		return redisExpired;
	}

	public SlidingWindowProperties setRedisExpired(long redisExpired) {
		this.redisExpired = redisExpired;
		return this;
	}

	public String getRedisContainerKey() {
		return redisContainerKey;
	}

	public SlidingWindowProperties setRedisContainerKey(String redisContainerKey) {
		this.redisContainerKey = redisContainerKey;
		return this;
	}

	public int getMaxNoticeThreadSize() {
		return maxNoticeThreadSize;
	}

	public SlidingWindowProperties setMaxNoticeThreadSize(int maxNoticeThreadSize) {
		this.maxNoticeThreadSize = maxNoticeThreadSize;
		return this;
	}

	public String getRedisPublisherKey() {
		return redisPublisherKey;
	}

	public SlidingWindowProperties setRedisPublisherKey(String redisPublisherKey) {
		this.redisPublisherKey = redisPublisherKey;
		return this;
	}

	public String getRedisTopicKey() {
		return redisTopicKey;
	}

	public SlidingWindowProperties setRedisTopicKey(String redisTopicKey) {
		this.redisTopicKey = redisTopicKey;
		return this;
	}
	
	
}
