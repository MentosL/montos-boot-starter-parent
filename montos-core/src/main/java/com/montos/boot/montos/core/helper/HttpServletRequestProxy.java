package com.jimistore.boot.nemo.core.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * HttpServletRequest代理类，优化request body只能读取一次
 * 
 * @author chenqi
 * @date 2019年4月23日
 *
 */
public class HttpServletRequestProxy extends HttpServletRequestWrapper {

	ServletInputStreamProxy servletInputStreamProxy;

	BufferedReader bufferedReader;

	String body;

	public HttpServletRequestProxy(HttpServletRequest request) {
		super(request);
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = request.getReader();
			String str;
			while ((str = br.readLine()) != null) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(str);
			}
			body = sb.toString();
			servletInputStreamProxy = new ServletInputStreamProxy(new ByteArrayInputStream(body.getBytes()));
			bufferedReader = new BufferedReader(new InputStreamReader(getInputStream()));
		} catch (Exception e) {

		}
	}

	public String getBody() {
		return body;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return bufferedReader;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return servletInputStreamProxy;
	}

	class ServletInputStreamProxy extends ServletInputStream {

		ByteArrayInputStream bais;

		public ServletInputStreamProxy(ByteArrayInputStream bais) {
			super();
			this.bais = bais;
		}

		@Override
		public boolean isFinished() {
			return false;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener) {

		}

		@Override
		public int read() throws IOException {
			return bais.read();
		}

		@Override
		public int readLine(byte[] b, int off, int len) throws IOException {
			return bais.read(b, off, len);
		}

	}

}
