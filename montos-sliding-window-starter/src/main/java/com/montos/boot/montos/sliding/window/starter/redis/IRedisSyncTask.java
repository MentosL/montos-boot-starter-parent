package com.montos.boot.montos.sliding.window.starter.redis;

/**
 * redis同步接口
 * @author chenqi
 * @Date 2018年8月27日
 *
 */
public interface IRedisSyncTask {
	
	/**
	 * 同步任务接口
	 */
	public default void sync(){};

}
