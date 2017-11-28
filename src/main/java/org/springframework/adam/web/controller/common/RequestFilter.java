/**
 * 
 */
package org.springframework.adam.web.controller.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author user
 *
 */
public class RequestFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// nothing to do
	}

	class MyHttpServletResponseWrapper extends HttpServletResponseWrapper {

		private int statusCode;

		public MyHttpServletResponseWrapper(HttpServletResponse response) {
			super(response);
			this.statusCode = 200; // 默认的状态是200
		}

		@Override
		public void sendError(int sc) throws IOException {
			statusCode = sc;
			super.sendError(sc);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			statusCode = sc;
			super.sendError(sc, msg);
		}

		@Override
		public void setStatus(int sc) {
			this.statusCode = sc;
			super.setStatus(sc);
		}

		public int getStatusCode() {
			return this.statusCode;
		}
	}
}
