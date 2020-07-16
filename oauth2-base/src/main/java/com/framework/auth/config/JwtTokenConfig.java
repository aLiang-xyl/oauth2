package com.framework.auth.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import com.framework.auth.service.ISysUserService;
import com.framework.auth.user.MyUserDetails;
import com.framework.auth.user.SysUser;

import lombok.extern.log4j.Log4j2;

/**
 * Oauth2认证相关配置
 * 
 * @author aLiang
 * @date 2020年4月2日
 */
@Log4j2
@Configuration
public class JwtTokenConfig {
	
	@Value("${oauth.jks.path}")
	private String jksPath;
	@Value("${oauth.jks.password}")
	private String jksPassword;
	@Value("${oauth.jks.key-pair-alias}")
	private String alias;
	
	@Autowired
	private MyRedisTokenStore myRedisTokenStore;
	
	@Autowired
	private ISysUserService sysUserService;

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
			@Override
			public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
				if (null != authentication.getUserAuthentication()) {
					MyUserDetails myUserDetails = (MyUserDetails) authentication.getUserAuthentication().getPrincipal();
					final Map<String, Object> additionalInformation = new HashMap<>();
					SysUser user = myUserDetails.getSysUser();
					log.info("login user:{}", user);
					sysUserService.updateLastLoginTime(user.getId());
					
					user.setPassword(null);
					additionalInformation.put("user", user);
					additionalInformation.put("roles", myUserDetails.getAuthorities());
					((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
					// save saveLoginLog
				}
				OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
				return enhancedToken;
			}
		};
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(jksPath), jksPassword.toCharArray());
		accessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(alias));
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(myRedisTokenStore);
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setTokenEnhancer(accessTokenConverter());
		
		return defaultTokenServices;
	}
}
