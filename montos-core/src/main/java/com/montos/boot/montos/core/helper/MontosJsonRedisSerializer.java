package com.montos.boot.montos.core.helper;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.core.util.ClassUtil;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

@SuppressWarnings("rawtypes")
public class NemoJsonRedisSerializer extends Jackson2JsonRedisSerializer {
	
	public class Cache{
		private String className;
		private String json;
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getJson() {
			return json;
		}
		public void setJson(String json) {
			this.json = json;
		}
		
	}
	@SuppressWarnings("unchecked")
	public NemoJsonRedisSerializer(Class type) {
		super(type);
	}

	public NemoJsonRedisSerializer(JavaType javaType) {
		super(javaType);
	}

	@Override
	public byte[] serialize(Object t) throws SerializationException {
		if(t==null){
			return new byte[0];
		} 
		ClassUtil.clearPersistentProxy(t);
		return super.serialize(t);
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		return super.deserialize(bytes);
	}

	@Override
	public void setObjectMapper(ObjectMapper objectMapper) {
		super.setObjectMapper(objectMapper);
	}

}
