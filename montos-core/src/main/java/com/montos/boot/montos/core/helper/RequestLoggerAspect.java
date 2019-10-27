package com.jimistore.boot.nemo.core.helper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.core.util.JsonString;

@Order(100)
@WebFilter(urlPatterns = "/*", filterName = "RequestLoggerFilter")
public class RequestLoggerAspect implements Filter {

	private final Logger logger = Logger.getLogger(getClass());

	public static final class Log {
		String code;
		String time;
		long process;
		String url;
		String param;
		String method;
		String ip;
		String user;
		long length;
		String thread;
		long start;
		long end;
		Object header;
		Object request;
		Object response;
		Object error;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public long getStart() {
			return start;
		}

		public void setStart(long start) {
			this.start = start;

			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(start);
			this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(c.getTime());
		}

		public long getEnd() {
			return end;
		}

		public void setEnd(long end) {
			this.end = end;
			this.process = end - start;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public Object getHeader() {
			return header;
		}

		public void setHeader(Object header) {
			this.header = header;
		}

		public Object getRequest() {
			return request;
		}

		public void setRequest(Object request) {
			this.request = request;
		}

		public Object getResponse() {
			return response;
		}

		public void setResponse(Object response) {
			this.response = response;
		}

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}

		public String getThread() {
			return thread;
		}

		public void setThread(String thread) {
			this.thread = thread;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public long getProcess() {
			return process;
		}

		public void setProcess(long process) {
			this.process = process;
		}

		public Object getError() {
			return error;
		}

		public void setError(Object error) {
			this.error = error;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getParam() {
			return param;
		}

		public void setParam(String param) {
			this.param = param;
		}

	}

	public ThreadLocal<Log> log = new ThreadLocal<Log>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			log.set(new Log());
			HttpServletRequest req = (HttpServletRequest) request;
			Thread thread = Thread.currentThread();
			Map<String, String> map = new HashMap<String, String>();
			@SuppressWarnings("rawtypes")
			Enumeration headerNames = req.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = req.getHeader(key);
				map.put(key, value);
			}

			log.get().setUrl(req.getRequestURL().toString());
			log.get().setParam(req.getQueryString());
			log.get().setMethod(req.getMethod());
			log.get().setIp(request.getRemoteAddr());
			log.get().setUser((String) Context.get(Context.CONTEXT_REQUEST_USER));
			log.get().setThread(new StringBuilder(thread.getName()).append("-").append(thread.getId()).toString());
			log.get().setHeader(map);
			log.get().setLength(request.getContentLengthLong());
			log.get().setStart(System.currentTimeMillis());

			if (request instanceof HttpServletRequestProxy) {
				HttpServletRequestProxy reqp = (HttpServletRequestProxy) request;
				log.get().setRequest(reqp.getBody());
			}
			// before
			chain.doFilter(request, response);
			// after
			log.get().setEnd(System.currentTimeMillis());
			if (response instanceof HttpServletResponseProxy) {
				HttpServletResponseProxy reqp = (HttpServletResponseProxy) response;
				log.get().setResponse(reqp.getBody());
			}
			log.get().setCode("200");
			logger.debug(JsonString.toJson(log.get()));
		} catch (Exception e) {
			// error
			log.get().setEnd(System.currentTimeMillis());
			log.get().setError(e.getMessage());
			log.get().setCode("500");
			logger.info(JsonString.toJson(log.get()));
		}
	}

	@Override
	public void destroy() {

	}

}
