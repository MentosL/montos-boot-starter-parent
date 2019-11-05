package com.montos.boot.montos.sliding.window.starter.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PublisherContainer implements IPublisherContainer {
	
	protected Map<String, Publisher> publisherMap = new HashMap<String, Publisher>();

	@Override
	public Collection<Publisher> listPublisher() {
		return publisherMap.values();
	}

	@Override
	public IPublisherContainer createPublisher(Publisher publisher) {
		if(publisherMap.containsKey(publisher.getKey())){
			throw new RuntimeException(String.format("publisher[%s] exist", publisher.getKey()));
		}
		publisherMap.put(publisher.getKey(), publisher);
		return this;
	}

	@Override
	public IPublisherContainer deletePublisher(String publisher) {
		if(!publisherMap.containsKey(publisher)){
			throw new RuntimeException(String.format("publisher[%s] not exist", publisher));
		}
		publisherMap.remove(publisher);
		return this;
	}

}
