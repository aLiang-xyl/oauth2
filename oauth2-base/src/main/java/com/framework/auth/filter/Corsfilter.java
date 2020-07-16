package com.framework.auth.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;

/**
 * @author aLiang
 * @date 2020年4月2日
 * 
 */
//@Configuration
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class Corsfilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
		res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
		res.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
		res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
			res.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, res);
		}
	}

}
