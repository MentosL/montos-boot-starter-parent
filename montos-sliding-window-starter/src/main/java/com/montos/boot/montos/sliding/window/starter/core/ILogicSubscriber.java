package com.montos.boot.montos.sliding.window.starter.core;

import java.util.Map;

/**
 * 复合计数订阅(topicMatch为值的spel表达式)
 * @author chenqi
 * @Date 2018年9月4日
 *
 */
public interface ILogicSubscriber extends IWarnSubscriber {
	
	/**
	 * 获取表达式(key为topic名称，value为变量名)
	 * @return
	 */
	public Map<String, String> getTopicVariableMap();

}
