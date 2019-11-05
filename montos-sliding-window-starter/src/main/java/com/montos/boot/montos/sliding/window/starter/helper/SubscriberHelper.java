package com.montos.boot.montos.sliding.window.starter.helper;

import com.montos.boot.montos.sliding.window.starter.annotation.Subscribe;
import com.montos.boot.montos.sliding.window.starter.core.*;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class SubscriberHelper {
	
	SlidingWindowTemplate slidingWindowTemplate;
	
	public SubscriberHelper setSlidingWindowTemplate(SlidingWindowTemplate slidingWindowTemplate) {
		this.slidingWindowTemplate = slidingWindowTemplate;
		return this;
	}
	
	public void createSubscriber(Subscribe subscribe, Method method, Object invoker){
		slidingWindowTemplate.subscribe(new ISubscriber(){

			@Override
			public String getTopicMatch() {
				return subscribe.value();
			}

			@Override
			public Integer getLength() {
				return subscribe.length();
			}

			@Override
			public TimeUnit getTimeUnit() {
				return subscribe.timeUnit();
			}

			@Override
			public Long getInterval() {
				return subscribe.interval();
			}

			@Override
			public Long getStart() {
				return subscribe.start();
			}

			@Override
			public INotice getNotice() {
				return new INotice(){
					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
					public void notice(INoticeEvent<?> event) {
						Class<?>[] types = method.getParameterTypes();
						Object[] params = new Object[types.length];
						for(int i=0;i<types.length;i++){
							if(INoticeStatisticsEvent.class.isAssignableFrom(types[i])){
								params[i] = new NoticeStatisticsEvent(event);
							}else if(INoticeEvent.class.isAssignableFrom(types[i])){
								params[i] = event;
							}else{
								params[i]=null;
							}
						}
						
						try {
							method.invoke(invoker, params);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						
					}
				};
			}
			
			
		});
	}

}
