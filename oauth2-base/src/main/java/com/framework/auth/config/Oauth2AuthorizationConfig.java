package com.framework.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.framework.auth.handler.CustomWebResponseExceptionTranslator;

/**
 * Oauth2认证相关配置
 * 
 * @author aLiang
 * @date 2020年4月2日
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
	
	@Value("${oauth.client.common.clinet-id}")
	private String commonCientId;
	@Value("${oauth.client.common.client-secret}")
	private String commonClientSecret;
	
	@Value("${oauth.client.weixin.clinet-id}")
	private String weixinClientId;
	@Value("${oauth.client.weixin.client-secret}")
	private String weixinClientSecret;
	
	@Value("${oauth.access-token-validity-seconds}")
	private int accessTokenValiditySeconds;
	@Value("${oauth.refresh-token-validity-seconds}")
	private int refreshTokenValiditySeconds;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	MyRedisTokenStore myRedisTokenStore;
	
	@Autowired
	CustomWebResponseExceptionTranslator customWebResponseExceptionTranslator;
	
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	@Autowired
	private DefaultTokenServices tokenServices;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()").allowFormAuthenticationForClients()
				.passwordEncoder(passwordEncoder);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		String[] grantTypes = {"client_credentials", "password", "refresh_token"};
		clients.inMemory().withClient(commonCientId).secret(passwordEncoder.encode(commonClientSecret)).authorizedGrantTypes(grantTypes).scopes("read", "write")
		        .and().withClient(weixinClientId).secret(passwordEncoder.encode(weixinClientSecret)).authorizedGrantTypes(grantTypes).scopes("read", "write")
		        .accessTokenValiditySeconds(accessTokenValiditySeconds) // token过期时间, 1d
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds); // refresh过期时间, 1d
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				.tokenStore(myRedisTokenStore).tokenServices(tokenServices)
				.authenticationManager(authenticationManager)
				.accessTokenConverter(accessTokenConverter).exceptionTranslator(customWebResponseExceptionTranslator) // 登录自定义错误
		;
	}
}
