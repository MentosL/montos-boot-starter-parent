package com.montos.boot.montos.sliding.window.starter.core;

public interface INoticeStatisticsEvent<T extends Number> extends INoticeEvent<T> {
	
	/**
	 * 获取最小值
	 * @return
	 */
	public T getMin();
	
	/**
	 * 获取最大值
	 * @return
	 */
	public T getMax();
	/**
	 * 获取累计值
	 * @return
	 */
	public T getSum();
	/**
	 * 获取平均值
	 * @return
	 */
	public T getAvg();
	
	/**
	 * 获取瞬时值
	 * @return
	 */
	public T getCur();

}
