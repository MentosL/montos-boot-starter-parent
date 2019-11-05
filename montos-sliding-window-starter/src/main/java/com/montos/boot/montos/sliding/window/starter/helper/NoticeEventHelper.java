package com.montos.boot.montos.sliding.window.starter.helper;

import com.montos.boot.montos.sliding.window.starter.core.INoticeEvent;
import com.montos.boot.montos.sliding.window.starter.core.INoticeStatisticsEvent;
import com.montos.boot.montos.sliding.window.starter.core.NoticeEvent;
import com.montos.boot.montos.sliding.window.starter.core.NoticeStatisticsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NoticeEventHelper {
	
	/**
	 * 合并窗口运算
	 * @param event
	 * @param length
	 * @return
	 */
	public static <T extends Number> INoticeEvent<T> mergeWindow(INoticeEvent<T> event, Integer length){
		NoticeEvent<T> noticeEvent = (NoticeEvent<T>) event;
		noticeEvent.setValue(NoticeEventHelper.mergeWindow(event.getValue(), length));
		return noticeEvent;
	}
	
	/**
	 * 转换时间单位运算
	 * @param srcList
	 * @param src
	 * @param dest
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> List<T> tranform(List<T> srcList, TimeUnit src, TimeUnit dest){
		long srcTimeUnit = src.toMillis(1);
		long destTimeUnit = dest.toMillis(1);
		if(srcTimeUnit==destTimeUnit){
			return srcList;
		}else if(srcTimeUnit < destTimeUnit){
			Long times = destTimeUnit / srcTimeUnit;
			int capacity = srcList.size() / times.intValue();
			
			List<T> value = new ArrayList<T>(capacity);
			for(int i=0;i<capacity;i++){
				Number t = 0;
				for(int j=0;j<times;j++){
					t = NumberUtil.add(t, srcList.get(times.intValue() * i + j));
				}
				value.add((T)t);
			}
			return value;
		}else{
			Long times = srcTimeUnit / destTimeUnit;
			int capacity = srcList.size() * times.intValue();
			
			List<T> value = new ArrayList<T>(capacity);
			for(int i=0;i<srcList.size();i++){
				for(int j=0;j<times;j++){
					value.add(srcList.get(i));
				}
			}
			return value;
		}
		
	}
	
	/**
	 * 合并窗口运算
	 * @param valueList
	 * @param length
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> List<T> mergeWindow(List<T> valueList, Integer length){
		if(length==1){
			return valueList;
		}
		if(valueList.size()<length){
			throw new RuntimeException("window's length must be less than capacity");
		}
		int capacity = valueList.size()-length+1;
		List<T> value = new ArrayList<T>(capacity);
		for(int i=0;i<capacity;i++){
			Number t = 0;
			for(int j=0;j<length;j++){
				t = NumberUtil.add(t, valueList.get(i+j));
			}
			value.add((T)t);
		}
		return value;
	}
	
	/**
	 * 计算最大最小平均累计瞬时值
	 * @param event
	 * @return
	 */
	public static <T extends Number> INoticeStatisticsEvent<T> count(INoticeEvent<T> event){
		return new NoticeStatisticsEvent<T>(event);
	}

}
