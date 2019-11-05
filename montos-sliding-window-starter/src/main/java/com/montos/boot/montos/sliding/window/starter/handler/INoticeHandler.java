package com.montos.boot.montos.sliding.window.starter.handler;

import com.montos.boot.montos.sliding.window.starter.core.INoticeEvent;

public interface INoticeHandler {
	
	/**
	 * 通知之前的处理
	 * @param event
	 */
	public void before(INoticeEvent<?> event);
	
	/**
	 * 通知之后的处理
	 * @param event
	 */
	public void after(INoticeEvent<?> event);

}
