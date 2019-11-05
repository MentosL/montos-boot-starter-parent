package com.montos.boot.montos.sliding.window.starter.core;

public interface INoticeWarnEvent<T extends Number> extends INoticeStatisticsEvent<T> {
	
	public boolean isWarn();

}
