package com.jimistore.boot.nemo.core.helper;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.core.annotation.Order;

@Order(11)
@WebFilter(urlPatterns = "/*", filterName = "InitContextFilter")
public class InitContextFilter implements Filter {

	private final Logger log = Logger.getLogger(getClass());

	/**
	 * 解析参数
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	private Object parse(HttpServletRequest request, String key) {
		Object value = request.getHeader(key);
		if (log.isTraceEnabled()) {
			log.trace(String.format("request user of header is %s", value));
		}
		if (value == null) {
			value = request.getParameter(key);
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("request user of param is %s", value));
		}
		if (value == null) {
			HttpSession session = request.getSession();
			value = session.getAttribute(key);
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("request user of session is %s", value));
		}
		return value;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Context.init();
		for (Context.Key key : Context.Key.values()) {
			try {
				String code = key.getCode();
				Object value = this.parse((HttpServletRequest) request, code);
				if (value != null) {
					Context.put(key, value);
				}
				if (log.isTraceEnabled()) {
					log.trace(String.format("initing context, %s = %s", code, value));
				}
			} catch (Exception e) {
				log.warn(String.format("initing context, parse %s error", key.getCode()));
			}
		}
		// 兼容老版本
		if (Context.get(Context.Key.CONTEXT_LOGIN_USER) != null) {
			Context.put(Context.Key.CONTEXT_REQUEST_USER, Context.get(Context.Key.CONTEXT_LOGIN_USER));
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
}
