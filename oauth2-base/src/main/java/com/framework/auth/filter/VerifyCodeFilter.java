package com.framework.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.auth.validate.ValidateCodeUtils;

/**
 * 验证码过滤器
 * 
 * @author aLiang
 * @date 2020年4月2日
 */
public class VerifyCodeFilter extends AbstractAuthenticationProcessingFilter {

	private AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();

	private boolean allowOnlyPost = false;

	@Autowired
	ValidateCodeUtils validateCodeUtils;

	public VerifyCodeFilter() {
		this("/oauth/token");
	}

	public VerifyCodeFilter(String path) {
		super(path);
		setRequiresAuthenticationRequestMatcher(new ClientCredentialsRequestMatcher(path));
		((OAuth2AuthenticationEntryPoint) authenticationEntryPoint).setTypeName("Form");
	}

	public void setAllowOnlyPost(boolean allowOnlyPost) {
		this.allowOnlyPost = allowOnlyPost;
	}

	/**
	 * @param authenticationEntryPoint the authentication entry point to set
	 */
	public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				if (exception instanceof BadCredentialsException) {
					exception = new BadCredentialsException(exception.getMessage(),
							new BadClientCredentialsException());
				}
				authenticationEntryPoint.commence(request, response, exception);
			}
		});
		setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				// no-op - just allow filter chain to continue to token endpoint
			}
		});
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		logger.info("------------VerifyCodeLoginAuthenticationFilter-----------------");
		if (allowOnlyPost && !"POST".equalsIgnoreCase(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[] { "POST" });
		}
		String isVerifyCodeStr = request.getParameter("isVerifyCode");

		boolean isVerifyCode = StringUtils.isEmpty(isVerifyCodeStr) ? false : Boolean.valueOf(isVerifyCodeStr);

		String verifyCode = request.getParameter("verifyCode");
		logger.info("request:verifyCode:" + verifyCode);

		boolean checkVerifyCode = checkVerifyCode(request, response, isVerifyCode, verifyCode);
		if (!checkVerifyCode) {
			throw new CredentialsExpiredException("验证码错误");
		}
		// If the request is already authenticated we can assume that this
		// filter is not needed
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ((authentication != null && authentication.isAuthenticated()) || !checkVerifyCode) {
			return authentication;
		}

		String clientId = request.getParameter("client_id");
		String clientSecret = request.getParameter("client_secret");
		if (clientId == null) {
			throw new BadCredentialsException("No client credentials presented");
		}

		if (clientSecret == null) {
			clientSecret = "";
		}

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId.trim(),
				clientSecret);
		return this.getAuthenticationManager().authenticate(authRequest);

	}

	private boolean checkVerifyCode(HttpServletRequest request, HttpServletResponse response, boolean isVerifyCode,
			String verifyCode) throws ServletException {
		//如果登录页面没有启用验证码，则不进入验证码验证
		if (!isVerifyCode) {
			return true;
		}
		boolean checkVerify = validateCodeUtils.checkVerify(request, verifyCode);
		logger.info("request checkVerify:" + checkVerify);
		if (!checkVerify) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", HttpServletResponse.SC_UNAUTHORIZED);
			map.put("msg", "验证码错误！");
			map.put("data", null);
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.writeValue(response.getOutputStream(), map);
				return checkVerify;
			} catch (Exception e) {
				throw new ServletException();
			}
		}
		return checkVerify;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}

	protected static class ClientCredentialsRequestMatcher implements RequestMatcher {

		private String path;

		public ClientCredentialsRequestMatcher(String path) {
			this.path = path;

		}

		@Override
		public boolean matches(HttpServletRequest request) {
			String uri = request.getRequestURI();
			int pathParamIndex = uri.indexOf(';');

			if (pathParamIndex > 0) {
				// strip everything after the first semi-colon
				uri = uri.substring(0, pathParamIndex);
			}

			String clientId = request.getParameter("client_id");

			if (clientId == null) {
				// Give basic auth a chance to work instead (it's preferred anyway)
				return false;
			}

			if ("".equals(request.getContextPath())) {
				return uri.endsWith(path);
			}

			return uri.endsWith(request.getContextPath() + path);
		}

	}
}
