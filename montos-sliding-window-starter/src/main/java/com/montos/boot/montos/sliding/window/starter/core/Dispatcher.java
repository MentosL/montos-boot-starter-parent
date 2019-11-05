package com.montos.boot.montos.sliding.window.starter.core;

import com.montos.boot.montos.sliding.window.starter.config.SlidingWindowProperties;
import com.montos.boot.montos.sliding.window.starter.handler.INoticeHandler;
import com.montos.boot.montos.sliding.window.starter.handler.IPublishHandler;
import com.montos.boot.montos.sliding.window.starter.helper.NoticeEventHelper;
import org.apache.log4j.Logger;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 调度器
 * 
 * @author chenqi
 * @Date 2018年7月19日
 *
 */
public class Dispatcher implements IDispatcher {

	private static final Logger log = Logger.getLogger(Dispatcher.class);

	public static final Long INTERVAL = 1000l;

	protected List<IPublishHandler> publishHandlerList = new ArrayList<IPublishHandler>();

	protected List<INoticeHandler> noticeHandlerList = new ArrayList<INoticeHandler>();

	protected ICounterContainer counterContainer;

	protected IChannelContainer channelContainer;

	protected IPublisherContainer publisherContainer;

	protected ITopicContainer topicContainer;

	protected SlidingWindowProperties slidingWindowProperties;

	protected Executor executor;

	protected LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

	// 队列线程
	Thread queueThread = new Thread("nemo-sliding-window-counter-container-queue") {

		@Override
		public void run() {
			while (true) {
				try {
					Runnable task = queue.take();
					task.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	};

	// 心跳线程
	Thread heartbeatThread = new Thread("nemo-sliding-window-heartbeat") {
		@Override
		public void run() {
			while (true) {
				try {
					heartbeat();
					Thread.sleep(INTERVAL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	};

	// 窗口调度线程
	Thread schedulerThread = new Thread("nemo-sliding-window-scheduler") {
		@Override
		public void run() {
			while (true) {
				try {
					scheduler();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {

					try {
						Thread.sleep(INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	};

	public Dispatcher() {
	}

	public Dispatcher setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	public Dispatcher setCounterContainer(ICounterContainer counterContainer) {
		this.counterContainer = counterContainer;
		return this;
	}

	public Dispatcher setChannelContainer(IChannelContainer channelContainer) {
		this.channelContainer = channelContainer;
		return this;
	}

	public Dispatcher setPublisherContainer(IPublisherContainer publisherContainer) {
		this.publisherContainer = publisherContainer;
		return this;
	}

	public Dispatcher setTopicContainer(ITopicContainer topicContainer) {
		this.topicContainer = topicContainer;
		return this;
	}

	public Dispatcher init() {
		heartbeatThread.setDaemon(true);
		schedulerThread.setDaemon(true);
		queueThread.setDaemon(true);
		heartbeatThread.start();
		schedulerThread.start();
		queueThread.start();

		executor = Executors.newFixedThreadPool(slidingWindowProperties.getMaxNoticeThreadSize());
		return this;
	}

	@Override
	public Dispatcher subscribe(ISubscriber subscriber) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				channelContainer.put(subscriber);
			}
		});
		return this;
	}

	@Override
	public Dispatcher publish(IPublishEvent<?> event) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				counterContainer.publish(event);
			}
		});
		return this;
	}

	@Override
	public IDispatcher addPublishHandler(IPublishHandler publishHandler) {
		publishHandlerList.add(publishHandler);
		return this;
	}

	@Override
	public IDispatcher addNoticeHandler(INoticeHandler noticeHandler) {
		noticeHandlerList.add(noticeHandler);
		return this;
	}

	/**
	 * 核心驱动线程实现
	 */
	protected void scheduler() {
		if (counterContainer == null) {
			return;
		}
		long eventTime = System.currentTimeMillis();
		try {
			for (IChannel channel : channelContainer.listAllChannel()) {
				if (!channel.ready()) {
					continue;
				}
				this.execute(eventTime, channel);
			}
		} finally {
			if (log.isTraceEnabled()) {
				log.trace(String.format("request scheduler end , cost time is %s",
						System.currentTimeMillis() - eventTime));
			}
		}
	}

	/**
	 * 解析事件数据
	 * 
	 * @param channel
	 * @param time
	 * @return
	 */
	protected INoticeEvent<?> parseValue(IChannel channel, Long time) {
		ISubscriber subscriber = channel.getSubscriber();
		String key = null;
		List<Number> value = null;
		List<Map<String, Number>> logicMapList = null;

		// 是否是逻辑主体订阅
		if (subscriber instanceof ILogicSubscriber) {
			ILogicSubscriber logicSubscriber = (ILogicSubscriber) subscriber;
			key = logicSubscriber.getTopicMatch();
			logicMapList = this.getLogicMapList(logicSubscriber);
			value = this.parseLogicValue(logicSubscriber, logicMapList);
		} else {
			key = channel.getTopicList().get(0);
			value = this.getValueByBuffered(key, subscriber.getTimeUnit(), subscriber.getLength(),
					subscriber.getValueType());
			// 合并二级窗口
			value = NoticeEventHelper.mergeWindow(value, subscriber.getSecondWindowLength());
		}
		NoticeEvent<Number> event = new NoticeEvent<Number>().setTopicKey(key).setValue(value).setTime(time);
		// 是否是逻辑订阅
		if (subscriber instanceof IWarnSubscriber) {
			return new NoticeLogicEvent<Number>(event).setOriginMap(logicMapList);
		}
		// 是否是预警订阅
		if (subscriber instanceof IWarnSubscriber) {
			return new NoticeStatisticsEvent<Number>(event);
		}
		return event;
	}

	/**
	 * 数据缓冲处理
	 * 
	 * @param key
	 * @param timeUnit
	 * @param length
	 * @param valueType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Number> getValueByBuffered(String key, TimeUnit timeUnit, Integer length, Class<?> valueType) {
		// TODO 缺缓冲实现
		return (List<Number>) counterContainer.window(key, timeUnit, length, valueType);
	}

	/**
	 * 获取逻辑主体的数据处理
	 * 
	 * @param logicSubscriber
	 * @return
	 */
	protected List<Map<String, Number>> getLogicMapList(ILogicSubscriber logicSubscriber) {
		long old = System.currentTimeMillis();
		List<Map<String, Number>> valueList = new ArrayList<Map<String, Number>>();
		try {

			List<Number> min = null;
			Class<?> valueType = logicSubscriber.getValueType();
			Map<String, String> variableMap = logicSubscriber.getTopicVariableMap();
			Map<String, List<Number>> dataMap = new HashMap<String, List<Number>>();

			// 读取所有需要参与计算的数据
			for (Entry<String, String> entry : variableMap.entrySet()) {
				if (valueType == null) {
					Topic topic = topicContainer.getTopic(entry.getKey());
					valueType = topic.getValueType();
				}
				List<Number> temp = this.getValueByBuffered(entry.getKey(), logicSubscriber.getTimeUnit(),
						logicSubscriber.getLength() + logicSubscriber.getSecondWindowLength() - 1, valueType);

				// 合并二级窗口
				temp = NoticeEventHelper.mergeWindow(temp, logicSubscriber.getSecondWindowLength());
				if (temp == null) {
					// 如果其中有一个数据集是空的，那么返回空的
					return valueList;
				}
				if (min == null || temp.size() < min.size()) {
					min = temp;
				}
				dataMap.put(entry.getKey(), temp);
			}
			// 计算逻辑的
			for (int i = 0; i < min.size(); i++) {
				Map<String, Number> logicMap = new HashMap<String, Number>();
				for (Entry<String, String> entry : variableMap.entrySet()) {
					logicMap.put(entry.getKey(), dataMap.get(entry.getKey()).get(i));
				}
				valueList.add(logicMap);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);

		} finally {

			if (log.isTraceEnabled()) {
				log.trace(String.format("get logic topic map , cost time is %s", System.currentTimeMillis() - old));
			}
		}

		return valueList;
	}

	/**
	 * 获取逻辑主体的数据处理
	 * 
	 * @param logicSubscriber
	 * @return
	 */
	protected List<Number> parseLogicValue(ILogicSubscriber logicSubscriber, List<Map<String, Number>> logicMapList) {
		long old = System.currentTimeMillis();
		List<Number> valueList = new ArrayList<Number>();
		Class<?> valueType = logicSubscriber.getValueType();
		Map<String, String> variableMap = logicSubscriber.getTopicVariableMap();
		try {
			// 计算逻辑的
			for (int i = 0; i < logicMapList.size(); i++) {
				Map<String, Number> dataMap = logicMapList.get(i);
				StandardEvaluationContext context = new StandardEvaluationContext();
				for (Entry<String, String> entry : variableMap.entrySet()) {
					context.setVariable(entry.getValue(), dataMap.get(entry.getKey()));
				}
				Number value = 0;
				try {
					value = (Number) this.parseExpression(context, logicSubscriber.getTopicMatch(), valueType);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				valueList.add(value);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);

		} finally {

			if (log.isTraceEnabled()) {
				log.trace(String.format("get logic topic value , cost time is %s", System.currentTimeMillis() - old));
			}
		}

		return valueList;
	}

	/**
	 * 执行任务
	 * 
	 * @param eventTime
	 * @param channel
	 */
	protected void execute(Long eventTime, IChannel channel) {

		executor.execute(new Runnable() {
			@Override
			public void run() {
				long old = System.currentTimeMillis();

				ISubscriber subscriber = channel.getSubscriber();
				String key = subscriber.getTopicMatch();
				try {
					if (log.isDebugEnabled()) {
						log.debug(String.format("call notice of subscriber[%s] start", key));
					}
					INoticeEvent<?> event = parseValue(channel, eventTime);

					// 是否做预警判断
					if (subscriber instanceof IWarnSubscriber) {
						IWarnSubscriber wsub = (IWarnSubscriber) subscriber;
						StandardEvaluationContext context = getContextByEvent(event);
						boolean result = parseExpression(context, wsub.getCondition(), Boolean.class);
						((NoticeStatisticsEvent<?>) event).setWarn(result);
						if (wsub.isOnlyNoticeWarn() && !result) {
							return;
						}
					}

					subscriber.getNotice().notice(event);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				} finally {
					long diff = System.currentTimeMillis() - old;
					if (log.isDebugEnabled()) {
						log.debug(String.format("call notice of subscriber[%s] end, cost time is %sms", key, diff));
					}
				}
			}

		});
	}

	/**
	 * 计数心跳
	 */
	public void heartbeat() {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if (counterContainer != null) {
					counterContainer.heartbeat();
				}
			}
		});
	}

	@Override
	public IDispatcher createCounter(Topic topic) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if (log.isDebugEnabled()) {
					log.debug(String.format("create counter %s", topic.getKey()));
				}
				counterContainer.createCounter(topic);
				channelContainer.put(topic.getKey());
			}
		});
		return this;
	}

	protected void createQueueTask(Runnable runnable) {
		try {
			queue.put(runnable);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IDispatcher createTopic(Topic topic) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if (log.isDebugEnabled()) {
					log.debug(String.format("create topic %s", topic.getKey()));
				}
				topicContainer.createTopic(topic);
				counterContainer.createCounter(topic);
				channelContainer.put(topic.getKey());
			}
		});
		return this;
	}

	@Override
	public IDispatcher deleteTopic(String topic) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if (log.isDebugEnabled()) {
					log.debug(String.format("delete topic %s", topic));
				}
				topicContainer.deleteTopic(topic);
				counterContainer.deleteCounter(topic);
				channelContainer.delete(topic);
			}
		});
		return this;
	}

	@Override
	public IDispatcher deleteCounter(String key) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if (counterContainer != null) {
					counterContainer.deleteCounter(key);
				}
			}
		});
		return this;
	}

	/**
	 * 获取函数spel对应初始化的上下文
	 * 
	 * @param event
	 * @return
	 */
	protected StandardEvaluationContext getContextByEvent(INoticeEvent<?> event) {

		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("event", event);
		if (event instanceof INoticeStatisticsEvent) {
			INoticeStatisticsEvent<?> sevent = (INoticeStatisticsEvent<?>) event;
			context.setVariable("max", sevent.getMax());
			context.setVariable("min", sevent.getMin());
			context.setVariable("sum", sevent.getSum());
			context.setVariable("avg", sevent.getAvg());
			context.setVariable("cur", sevent.getCur());
		}

		return context;
	}

	/**
	 * spel格式化
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	protected <T> T parseExpression(StandardEvaluationContext context, String str, Class<T> clazz) {
		return new SpelExpressionParser().parseExpression(str).getValue(context, clazz);
	}

	@Override
	public IDispatcher unsubscribe(ISubscriber subscriber) {
		channelContainer.delete(subscriber);
		return this;
	}

}
