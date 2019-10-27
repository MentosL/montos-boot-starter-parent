package com.montos.boot.montos.core.helper;

import java.util.HashMap;
import java.util.Map;

public class Context {

	public enum Key {
		CONTEXT_LOGIN_USER("userId"), CONTEXT_REQUEST_USER("request-user"),
		CONTEXT_TRANSITION_ID("request-transition-id"), CONTEXT_SLEUTH_ID("request-sleuth-id");

		String code;

		String alias;

		private Key(String code) {
			this.code = code;
		}

		private Key(String code, String alias) {
			this.code = code;
			this.alias = alias;
		}

		public String getCode() {
			return code;
		}

		public String getAlias() {
			return alias;
		}

	}

	private static final ThreadLocal<ContextStore> context = new ThreadLocal<ContextStore>();

	/**
	 * 请求用户
	 */
	public static final String CONTEXT_REQUEST_USER = Key.CONTEXT_REQUEST_USER.getCode();

	/**
	 * 请求的事务编号
	 */
	public static final String CONTEXT_TRANSITION_ID = Key.CONTEXT_TRANSITION_ID.getCode();

	/**
	 * 请求的链路编号
	 */
	public static final String CONTEXT_SLEUTH_ID = Key.CONTEXT_SLEUTH_ID.getCode();

	private static class ContextStore {
		Map<String, Object> map = new HashMap<String, Object>();

		public Object get(String key) {
			return map.get(key);
		}

		public void put(String key, Object value) {
			map.put(key, value);
		}
	}

	public static Object get(String key) {
		if (context.get() == null) {
			Context.init();
		}
		return context.get().get(key);
	}

	public static Object get(Key key) {

		return Context.get(key.getCode());
	}

	public static void put(Key key, Object value) {
		Context.put(key.getCode(), value);
	}

	public static void put(String key, Object value) {
		if (context.get() == null) {
			Context.init();
		}
		context.get().put(key, value);
	}

	public static void init() {
		context.set(new ContextStore());
	}
}
