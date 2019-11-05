package com.montos.boot.montos.sliding.window.starter.redis;

import com.montos.boot.montos.sliding.window.starter.config.SlidingWindowProperties;
import com.montos.boot.montos.sliding.window.starter.core.Counter;
import com.montos.boot.montos.sliding.window.starter.core.ICounter;
import com.montos.boot.montos.sliding.window.starter.helper.NumberUtil;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class RedisCounter<T> extends Counter<T> implements ICounter<T>, IRedisSyncTask {
		
	private static final Logger log = Logger.getLogger(RedisCounter.class);

	private SlidingWindowProperties slidingWindowProperties;
	
	Class<?> valueType;
	
	Map<Long, Number> remoteMap = new HashMap<Long, Number>();
	
	@SuppressWarnings("rawtypes")
	RedisTemplate redisTemplate;
	
	private RedisCounter(){
		super();
	}
	
	
	public RedisCounter<T> setValueType(Class<?> valueType) {
		this.valueType = valueType;
		return this;
	}


	public RedisCounter<T> setRedisTemplate(RedisTemplate<?, ?> redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	private RedisCounter<T> setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}


	public RedisCounter<T> setTimeUnit(TimeUnit timeUnit) {
		super.setTimeUnit(timeUnit);
		return this;
	}


	public RedisCounter<T> setCapacity(int capacity) {
		super.setCapacity(capacity);
		return this;
	}


	@Override
	public RedisCounter<T> setKey(String key) {
		super.setKey(key);
		return this;
	}


	public static <E> RedisCounter<E> create(SlidingWindowProperties slidingWindowProperties, RedisTemplate<?,?> redisTemplate, String key, TimeUnit timeUnit, Integer capacity, Class<E> valueType){
		return new RedisCounter<E>().setSlidingWindowProperties(slidingWindowProperties).setRedisTemplate(redisTemplate).setKey(key).setTimeUnit(timeUnit).setCapacity(capacity).setValueType(valueType).init();
	}

	@SuppressWarnings("unchecked")
	protected RedisCounter<T> init(){
		//初始化开始
		redisTemplate.opsForHash().putIfAbsent(this.getRedisKey(), String.valueOf(START_KEY), String.valueOf(System.currentTimeMillis()));
		redisTemplate.expire(this.getRedisKey(), slidingWindowProperties.getRedisExpired(), TimeUnit.MILLISECONDS);
		long value = Long.parseLong(redisTemplate.opsForHash().get(this.getRedisKey(), String.valueOf(START_KEY)).toString());
		this.setStart(value);
		
		//增加冗余空间
		super.setCapacity(this.getCapacity()+slidingWindowProperties.getExpiredCapacity()+slidingWindowProperties.getExpiredOffset());
		
		return this;
	}
	
	@Override
	protected Counter<T> setStart(long start) {
		remoteMap.put(START_KEY, start);
		return super.setStart(start);
	}


	/**
	 * 同步到redis
	 */
	@SuppressWarnings("unchecked")
	public void sync(){
		log.debug("request sync");
		
		//上传计数
		Map<Long,Number> cloneValueMap = new HashMap<Long,Number>();
		synchronized (valueMap) {
			cloneValueMap.putAll(valueMap);
			valueMap = new HashMap<Long, Number>();
			valueMap.put(START_KEY, this.getStart());
		}
		for(Entry<Long,Number> entry:cloneValueMap.entrySet()){
			if(entry.getValue()!=null&&!entry.getValue().equals(0)&&!entry.getKey().equals(START_KEY)){
				redisTemplate.opsForHash().increment(this.getRedisKey(), entry.getKey().toString(), entry.getValue().intValue());
			}
		}
		
		//清除失效计数
		long now = System.currentTimeMillis();
		long curr = this.getIndex(this.getStart(), now);
		Object[] expiredKeys = new String[slidingWindowProperties.getExpiredCapacity()];
		for(int i=0;i<slidingWindowProperties.getExpiredCapacity();i++){
			long index = curr + i +slidingWindowProperties.getExpiredOffset();
			if(index>=getCapacity()){
				index = index % getCapacity();
			}
			expiredKeys[i] = String.valueOf(index);
		}
		redisTemplate.opsForHash().delete(this.getRedisKey(), expiredKeys);
		if(log.isDebugEnabled()){
			StringBuffer sb = new StringBuffer("[");
			for(Object str:expiredKeys){
				sb.append(str).append(",");
			}
			sb.append("]");
			log.debug(String.format("clear invalid counter[%s] key:%s", this.getKey(), sb.toString()));
		}
		

		
		//下载计数
		Map<Long, Number> map = this.getByRedis();
		if(map!=null){
			synchronized (remoteMap) {
				remoteMap = map;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<Long, Number> getByRedis(){
		Map<String, String> src = redisTemplate.opsForHash().entries(this.getRedisKey());
		if(src!=null){
			Map<Long, Number> dest = new HashMap<Long, Number>();
			for(Entry<String, String> entry:src.entrySet()){
				dest.put(Long.parseLong(entry.getKey()), NumberUtil.parse(entry.getValue(), Long.class));
			}
			return dest;
		}
		return null;
		
		
	}


	@Override
	public <E> List<E> window(TimeUnit timeUnit, Integer length, Class<E> valueType, long timestamp) {
		// TODO Auto-generated method stub
		return super.window(remoteMap, timeUnit, length, valueType, timestamp, slidingWindowProperties.getSyncInterval());
	}


	@Override
	public <E> List<E> window(TimeUnit timeUnit, Integer length, Class<E> valueType) {
		long now = System.currentTimeMillis();
		return super.window(remoteMap, timeUnit, length, valueType, now, slidingWindowProperties.getSyncInterval());
	}
	
	private String getRedisKey(){
		return String.format("%s-%s", slidingWindowProperties.getRedisContainerKey(), super.getKey());
	}
}
