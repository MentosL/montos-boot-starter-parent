package com.montos.boot.montos.sliding.window.starter.core;

import com.jimistore.boot.nemo.sliding.window.helper.NumberUtil;

import java.util.List;

public class NoticeStatisticsEvent<T extends Number> extends NoticeEvent<T> implements INoticeStatisticsEvent<T>, INoticeWarnEvent<T> {
		
	private T min,max,sum,avg,cur;
	
	private boolean warn;
	
	@SuppressWarnings("unchecked")
	public NoticeStatisticsEvent(INoticeEvent<T> noticeEvent){
		NoticeEvent<T> event = (NoticeEvent<T>) noticeEvent;
		
		this.setSubscriber(event.getSubscriber())
		.setTopicKey(event.getTopicKey())
		.setValue(event.getValue())
		.setTime(event.getTime());
		
		if(noticeEvent instanceof INoticeWarnEvent){
			this.setWarn(((INoticeWarnEvent<?>)noticeEvent).isWarn());
		}
		List<T> list = noticeEvent.getValue();
		
		for(T t:list){
			if(t==null||t.equals(Double.NaN)){
				continue ;
			}
			if(min==null){
				min=t;
				max=t;
				sum=t;
			}else{

				if(NumberUtil.compare(min, t)>0){
					min=t;
				}
				if(NumberUtil.compare(max, t)<0){
					max=t;
				}
				sum = (T) NumberUtil.add(sum, t);
			}
		}
		if(list.size()>0) {
			cur = list.get(0);
		}
		avg = (T) NumberUtil.except(sum, list.size());
	}

	@Override
	public T getMin() {
		
		return min;
	}

	@Override
	public T getMax() {
		
		return max;
	}

	@Override
	public T getSum() {
		
		return sum;
	}

	@Override
	public T getAvg() {
		
		return avg;
	}

	public T getCur() {
		return cur;
	}

	public boolean isWarn() {
		return warn;
	}

	public NoticeStatisticsEvent<T> setWarn(boolean warn) {
		this.warn = warn;
		return this;
	}
	
	 

}
