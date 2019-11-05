package com.montos.boot.montos.sliding.window.starter.core;

public interface INotice {
	
	/**
	 * 通知
	 * @param event
	 */
	public void notice(INoticeEvent<?> event);

}
