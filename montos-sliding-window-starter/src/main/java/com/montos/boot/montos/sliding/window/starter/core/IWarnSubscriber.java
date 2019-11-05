package com.montos.boot.montos.sliding.window.starter.core;

public interface IWarnSubscriber extends ISubscriber,IWarn {
	
	/**
	 * 是否只在告警时推送
	 * @return
	 */
	public default boolean isOnlyNoticeWarn(){
		return true;
	}

}
