package com.framework.auth.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.framework.auth.filter.VerifyCodeFilter;
import com.framework.auth.handler.AuthAccessDeniedHandler;
import com.framework.auth.handler.AuthExceptionEntryPoint;
import com.google.common.collect.Maps;

/**
 * 身份安全认证
 * 
 * @author aliang
 * @date 2020年4月23日下午2:21:28
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static String CLIENT_ID = "client_id";

	@Value("${oauth.path-matchers.permit-all}")
	private String[] permitAll;

	@Value("${oauth.client.common.clinet-id}")
	private String commonCientId;

	@Value("${oauth.client.weixin.clinet-id}")
	private String weixinClientId;

	@Autowired
	private UserDetailsService commonUserDetailsService;

	@Autowired
	private UserDetailsService wxUserDetailsService;
	
	@Autowired
	private JwtDecoder jwtDecoder;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new SCryptPasswordEncoder();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.anonymous().disable()
		    .addFilterBefore(getVerifyCodeFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests(authorize -> authorize.antMatchers(HttpMethod.OPTIONS).permitAll()
						.antMatchers(permitAll).permitAll().anyRequest().authenticated()
						)
			.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
			.exceptionHandling()
			.authenticationEntryPoint(new AuthExceptionEntryPoint())
			.accessDeniedHandler(new AuthAccessDeniedHandler())
			.and()
			.formLogin().disable()
			.csrf(csrf -> csrf.disable()).headers().frameOptions().disable();
		//添加资源校验处理
		OAuth2ResourceServerConfigurer<?> configurer = http.getConfigurer(OAuth2ResourceServerConfigurer.class);
		configurer.authenticationEntryPoint(new AuthExceptionEntryPoint());
	}

//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers(permitAll);
//	}

//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(commonDaoAuhthenticationProvider());
//	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
		return myAuthenticationManager();
	}

	@Bean
	public VerifyCodeFilter getVerifyCodeFilter() throws Exception {
		VerifyCodeFilter filter = new VerifyCodeFilter();
		filter.setAuthenticationManager(this.authenticationManagerBean());
		return filter;
	}
	
	/**
	 * 自定义权限管理器,根据不同的client_id调用不同的处理
	 * 
	 * @return
	 */
	private MyAuthenticationManager myAuthenticationManager() {
		JwtAuthenticationProvider jwtProvider = new JwtAuthenticationProvider(jwtDecoder);
		MyAuthenticationManager myAuthenticationManager = new MyAuthenticationManager(jwtProvider);
		myAuthenticationManager.putProvider(commonCientId, commonDaoAuhthenticationProvider());
		myAuthenticationManager.putProvider(weixinClientId, wxDaoAuhthenticationProvider());
		
		return myAuthenticationManager;
	}

	/**
	 * 普通用户权限处理
	 * 
	 * @return
	 */
	private AuthenticationProvider commonDaoAuhthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(commonUserDetailsService);
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	/**
	 * 微信权限处理
	 * 
	 * @return
	 */
	private AuthenticationProvider wxDaoAuhthenticationProvider() {
		WxDaoAuthenticationProvider daoAuthenticationProvider = new WxDaoAuthenticationProvider(wxUserDetailsService);
		return daoAuthenticationProvider;
	}

	/**
	 * 自定义manager，可根据不同的client_id来做不同的处理
	 * 
	 * @author xing
	 *
	 */
	static final class MyAuthenticationManager implements AuthenticationManager {

		private Map<String, AuthenticationProvider> providerMap = Maps.newHashMap();
		private JwtAuthenticationProvider jwtProvider;
		
		public MyAuthenticationManager(JwtAuthenticationProvider jwtProvider) {
			super();
			this.jwtProvider = jwtProvider;
		}

		public MyAuthenticationManager putProvider(String clientId, AuthenticationProvider provider) {
			providerMap.put(clientId, provider);
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
			Class<? extends Authentication> toTest = authentication.getClass();
			if (jwtProvider.supports(toTest)) {
				//当header中有token时，则会使用jwtProvider来处理
				Authentication authenticate = jwtProvider.authenticate(authentication);
				return authenticate;
			} else {
				Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
				//根据不同的client_id调用不同的处理
				Object clientId = details.get(CLIENT_ID);
				if (clientId == null) {
					throw new IllegalArgumentException("not found param client_id");
				}
				AuthenticationProvider authenticationProvider = providerMap.get(clientId.toString());
				if (authenticationProvider == null) {
					throw new BadCredentialsException("invalid client_id: AuthenticationProvider is not queried based on client_id");
				}
				Authentication authenticate = authenticationProvider.authenticate(authentication);
				return authenticate;
			}
		}
	}

	/**
	 * 微信小程序登录处理，把js_code作为用户名调用微信接口查询openid，查询到则登录成功
	 * 
	 * @author xing
	 *
	 */
	static final class WxDaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

		private UserDetailsService userDetailsService;

		public WxDaoAuthenticationProvider(UserDetailsService wxUserDetailsService) {
			this.userDetailsService = wxUserDetailsService;
		}

		@Override
		protected void additionalAuthenticationChecks(UserDetails userDetails,
				UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
			// 微信小程序用户不进行密码校验
		}

		/**
		 * 这里的用户名是js_code
		 */
		@Override
		protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
				throws AuthenticationException {
			try {
				return this.userDetailsService.loadUserByUsername(username);
			} catch (UsernameNotFoundException ex) {
				throw new BadCredentialsException(ex.getMessage());
			} catch (InternalAuthenticationServiceException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
			}
		}
	}
}