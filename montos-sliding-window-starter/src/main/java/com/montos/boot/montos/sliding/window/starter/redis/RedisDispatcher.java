package com.montos.boot.montos.sliding.window.starter.redis;

import com.montos.boot.montos.sliding.window.starter.core.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步redis数据
 * @author chenqi
 * @Date 2018年7月17日
 *
 */
public class RedisDispatcher extends Dispatcher implements IRedisSyncTask {
	
	private static final Logger log = Logger.getLogger(RedisDispatcher.class);
	
	List<IRedisSyncTask> redisSyncTaskList = new ArrayList<IRedisSyncTask>();
	
	//同步远端数据线程
	Thread syncThread = new Thread("nemo-sliding-window-redis-sync"){
		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(slidingWindowProperties.getSyncInterval());
					if(redisSyncTaskList!=null){
						for(IRedisSyncTask redisSyncTask:redisSyncTaskList){
							createQueueTask(new Runnable() {
								@Override
								public void run() {
									redisSyncTask.sync();
								}
							});
						}
					}
				} catch (Exception e) {
					log.error("redis sync error", e);
				}
			}
		}
	};

	public RedisDispatcher() {
		super();
	}

	public RedisDispatcher setRedisSyncTaskList(List<IRedisSyncTask> redisSyncTaskList) {
		this.redisSyncTaskList = redisSyncTaskList;
		return this;
	}

	public void sync(){
		if(counterContainer!=null&&counterContainer instanceof RedisCounterContainer){
			RedisCounterContainer redisCounterContainer = (RedisCounterContainer) counterContainer;
			log.debug("request sync");
			//同步缺少的计数器
			List<CounterMsg> counterMsgList = redisCounterContainer.getNotExistCounterList();
			for(CounterMsg counterMsg:counterMsgList){
				Topic topic = new Topic()
						.setKey(counterMsg.getKey())
						.setTimeUnitStr(counterMsg.getTimeUnit())
						.setCapacity(counterMsg.getCapacity())
						.setClassName(counterMsg.getClassName());
				
				createCounter(topic);

			}
			//同步已经被删除的计数器
			List<ICounter<?>> counterList = redisCounterContainer.getOverflowCounterList();
			for(ICounter<?> counter:counterList){
				redisCounterContainer.deleteCounter(counter.getKey());
			}
		}
		
	}

	@Override
	public RedisDispatcher init() {
		super.init();
		
		this.addSyncTask(this);
		
		syncThread.setDaemon(true);
		syncThread.start();
		return this;
	}
	
	public void addSyncTask(IRedisSyncTask task){
		redisSyncTaskList.add(task);
	}

	@Override
	public Dispatcher setCounterContainer(ICounterContainer counterContainer) {
		super.setCounterContainer(counterContainer);
		if(counterContainer instanceof IRedisSyncTask){
			this.addSyncTask((IRedisSyncTask)counterContainer);
		}
		return this;
	}

	@Override
	public Dispatcher setPublisherContainer(IPublisherContainer publisherContainer) {
		super.setPublisherContainer(publisherContainer);
		if(publisherContainer instanceof IRedisSyncTask){
			this.addSyncTask((IRedisSyncTask)publisherContainer);
		}
		return this;
	}

	@Override
	public Dispatcher setTopicContainer(ITopicContainer topicContainer) {
		super.setTopicContainer(topicContainer);
		if(topicContainer instanceof IRedisSyncTask){
			this.addSyncTask((IRedisSyncTask)topicContainer);
		}
		return this;
	}

}
