package com.montos.boot.montos.sliding.window.starter.core;

import java.util.Collection;

public interface IPublisherContainer {
	
	/**
	 * 获取所有的发布者
	 * @param method
	 * @param targetClass
	 * @return
	 */
	public Collection<Publisher> listPublisher();
	
	/**
	 * 创建一个publisher
	 * @param publisher
	 */
	public IPublisherContainer createPublisher(Publisher publisher);
	
	/**
	 * 删除publisher
	 * @param publisher
	 */
	public IPublisherContainer deletePublisher(String publisherKey);
	
	

}
