package com.montos.boot.montos.sliding.window.starter.redis;

public class CounterMsg {
	
	String key;
	
	int capacity;
	
	String timeUnit;
	
	String className;

	public String getKey() {
		return key;
	}

	public CounterMsg setKey(String key) {
		this.key = key;
		return this;
	}

	public int getCapacity() {
		return capacity;
	}

	public CounterMsg setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public CounterMsg setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public CounterMsg setClassName(String className) {
		this.className = className;
		return this;
	}

}
