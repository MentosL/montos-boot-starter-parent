package com.jimistore.boot.nemo.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * HttpServletResponse代理类
 * 
 * @author chenqi
 * @date 2019年4月23日
 *
 */
public class HttpServletResponseProxy extends HttpServletResponseWrapper {

	Body body;

	ServletOutputStreamProxy servletOutputStreamProxy;

	public HttpServletResponseProxy(HttpServletResponse response) {
		super(response);
	}

	public String getBody() {
		return body.getBody();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (servletOutputStreamProxy == null) {
			servletOutputStreamProxy = new ServletOutputStreamProxy(super.getResponse().getOutputStream());
			body = servletOutputStreamProxy;
		}
		return servletOutputStreamProxy;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		PrintWriterProxy printWriterProxy = new PrintWriterProxy(super.getWriter());
		body = printWriterProxy;
		return printWriterProxy;
	}

	interface Body {
		public String getBody();
	}

	class PrintWriterProxy extends PrintWriter implements Body {

		PrintWriter writer;

		ServletOutputStream servletOutputStream;

		StringWriter stringWriter;

		public PrintWriterProxy(PrintWriter writer) {
			super(writer);
			this.writer = writer;
			this.stringWriter = new StringWriter();
		}

		@Override
		public void write(char[] buf, int off, int len) {
			writer.write(buf, off, len);
			stringWriter.write(buf, off, len);
		}

		@Override
		public void write(char[] buf) {
			writer.write(buf);
			try {
				stringWriter.write(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void write(int c) {
			writer.write(c);
			stringWriter.write(c);
		}

		@Override
		public void write(String s, int off, int len) {
			writer.write(s, off, len);
			stringWriter.write(s, off, len);
		}

		@Override
		public void write(String s) {
			writer.write(s);
			stringWriter.write(s);
		}

		@Override
		public String getBody() {
			return stringWriter.toString();
		}

	}

	class ServletOutputStreamProxy extends ServletOutputStream implements Body {

		ByteArrayOutputStream baos;

		ServletOutputStream servletOutputStream;

		public ServletOutputStreamProxy(ServletOutputStream servletOutputStream) {
			super();
			this.servletOutputStream = servletOutputStream;
			this.baos = new ByteArrayOutputStream();
		}

		@Override
		public boolean isReady() {
			return servletOutputStream.isReady();
		}

		@Override
		public void setWriteListener(WriteListener listener) {
			servletOutputStream.setWriteListener(listener);
		}

		@Override
		public void write(int b) throws IOException {
			servletOutputStream.write(b);
			baos.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			servletOutputStream.write(b);
			baos.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			servletOutputStream.write(b, off, len);
			baos.write(b, off, len);
		}

		@Override
		public void close() throws IOException {
			servletOutputStream.close();
		}

		@Override
		public void flush() throws IOException {
			servletOutputStream.flush();
		}

		@Override
		public String getBody() {
			return baos.toString();
		}

	}

}
