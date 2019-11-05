package com.montos.boot.montos.sliding.window.starter.core;

public class Publisher {
	
	String service;
	
	String key;
	
	String alias;

	public String getKey() {
		return key;
	}

	public Publisher setKey(String key) {
		this.key = key;
		return this;
	}

	public String getService() {
		return service;
	}

	public Publisher setService(String service) {
		this.service = service;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public Publisher setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	
	
}
