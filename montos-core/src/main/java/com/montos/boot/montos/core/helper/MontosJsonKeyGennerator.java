package com.montos.boot.montos.core.helper;

import com.jimistore.boot.nemo.core.util.JsonString;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

public class NemoJsonKeyGennerator implements KeyGenerator {
	
	class Key{
		String className;
		
		String methodName;
		
		Object[] param;

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public Object[] getParam() {
			return param;
		}

		public void setParam(Object[] param) {
			this.param = param;
		}
	}
	
	public Object generate(Object target, Method method, Object... params) {
	    Key key = new Key();
	    key.setClassName(target.getClass().getName());
	    key.setMethodName(method.getName());
	    key.setParam(params);
	    return JsonString.toJson(key);
	    }
}
