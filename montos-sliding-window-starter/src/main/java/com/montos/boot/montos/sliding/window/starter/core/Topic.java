package com.montos.boot.montos.sliding.window.starter.core;

import com.montos.boot.montos.sliding.window.starter.helper.TimeUnitParser;

import java.util.concurrent.TimeUnit;

public class Topic implements Cloneable {
	
	String publisherKey;
	
	String condition;
	
	String num;
	
	String key;
	
	Integer capacity;
	
	String timeUnitStr;
	
	String className;
	
	Class<?> valueType;
	
	TimeUnit timeUnit;
	
	boolean fixed;
	
	public static Topic from(com.montos.boot.montos.sliding.window.starter.annotation.Topic topic){
		return new Topic()
		.setFixed(true)
		.setKey(topic.value())
		.setTimeUnit(topic.timeUnit())
		.setCapacity(topic.capacity())
		.setCondition(topic.condition())
		.setNum(topic.num())
		.setValueType(topic.valueType());
	}

	public String getKey() {
		return key;
	}

	public Topic setKey(String key) {
		this.key = key;
		return this;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public Topic setCapacity(Integer capacity) {
		this.capacity = capacity;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public String getPublisherKey() {
		return publisherKey;
	}

	public Topic setPublisherKey(String publisherKey) {
		this.publisherKey = publisherKey;
		return this;
	}

	public String getCondition() {
		return condition;
	}

	public Topic setCondition(String condition) {
		this.condition = condition;
		return this;
	}

	public String getNum() {
		return num;
	}

	public Topic setNum(String num) {
		this.num = num;
		return this;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public String getTimeUnitStr() {
		return timeUnitStr;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public Topic setClassName(String className) {
		this.className = className;
		if(className!=null){
			try {
				valueType = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return this;
	}

	public Topic setTimeUnitStr(String timeUnitStr) {
		this.timeUnitStr = timeUnitStr;
		if(timeUnitStr!=null){
			this.timeUnit = TimeUnitParser.parse(timeUnitStr);
		}
		return this;
	}

	public Topic setValueType(Class<?> valueType) {
		this.valueType = valueType;
		if(valueType!=null){
			this.className = valueType.getName();
		}
		return this;
	}

	public Topic setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
		if(timeUnit!=null){
			this.timeUnitStr = TimeUnitParser.parse(timeUnit);
		}
		return this;
	}

	public boolean isFixed() {
		return fixed;
	}

	public Topic setFixed(boolean fixed) {
		this.fixed = fixed;
		return this;
	}

	@Override
	public Topic clone() throws CloneNotSupportedException {
		return (Topic) super.clone();
	}
	
	

}
