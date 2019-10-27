package com.montos.boot.montos.core.helper;

import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(12)
@WebFilter(urlPatterns = "/*", filterName = "RequestProxyFilter")
public class RequestProxyFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			chain.doFilter(new HttpServletRequestProxy((HttpServletRequest) request),
					new HttpServletResponseProxy((HttpServletResponse) response));
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {

	}

}
