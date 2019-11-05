package com.montos.boot.montos.sliding.window.starter.handler;

import com.montos.boot.montos.sliding.window.starter.core.IPublishEvent;

public interface IPublishHandler {
	
	/**
	 * 发布之前的处理
	 * @param event
	 */
	public void before(IPublishEvent<?> event);
	
	/**
	 * 发布之后的处理
	 * @param event
	 */
	public void after(IPublishEvent<?> event);

}
