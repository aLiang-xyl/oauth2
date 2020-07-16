//package com.framework.auth.controller;
//
//import java.util.Map;
//import java.util.Set;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.codec.digest.DigestUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.common.OAuth2RefreshToken;
//import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.OAuth2Request;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.beust.jcommander.internal.Sets;
//import com.framework.auth.service.impl.WxUserDetailsService;
//import com.google.common.collect.Maps;
//
//import springfox.documentation.annotations.ApiIgnore;
//
///**
// * <p>
// * 微信小程序登陆
// * </p>
// * 
// * @author aLiang
// * @since 2020-04-23
// */
//@ConditionalOnProperty(prefix = "oauth.weixin.small-program", name = "appid", matchIfMissing = false)
//@RestController
//public class WeixinLoginController {
//	
//	private static final Set<String> SCOPE;
//	private static final String USERNAME = "username";
//	private static final String PASSWORD = "password";
//	private static final String CLIENT_ID = "client_id";
//	private static final String CLIENT_SECRET = "client_secret";
//	private static final String GRANT_TYPE = "grant_type";
//	private static final String ACCESS_TOKEN = "access_token";
//	private static final String REFRESH_TOKEN = "refresh_token";
//	
//	@Value("${oauth.client.weixin.clinet-id}")
//	private String wxClientId;
//	@Value("${oauth.client.weixin.client-secret}")
//	private String wxClientSecret;
//	
//	@Autowired
//	DefaultTokenServices tokenServices;
//	@Autowired
//	private JwtAccessTokenConverter accessTokenConverter;
//	@Autowired
//	private WxUserDetailsService wxUserDetailsService;
//
//	static {
//		SCOPE = Sets.newHashSet();
//		SCOPE.add("read");
//		SCOPE.add("write");
//	}
//	
//	/**
//	 * 小程序登陆，根据js_code调用微信接口获取openid，根据openid查询用户表（若未查询到则新增），每个微信用户对应一个用户
//	 * 
//	 * @param response
//	 * @param client_id
//	 * @param client_secret
//	 * @param grant_type
//	 * @param js_code
//	 * @return
//	 */
//	@ApiIgnore
//	@SuppressWarnings("unchecked")
//	@PostMapping(value = { "/oauth/wx_token" })
//	public Map<String, Object> wxToken(HttpServletResponse response, String client_id, String client_secret, String grant_type, String js_code) {
//		if (!wxClientId.equals(client_id) || !wxClientSecret.equals(client_secret)) {
//			response.setStatus(HttpStatus.UNAUTHORIZED.value());
//			throw new UnauthorizedClientException("undefined client_id or client_secret");
//		}
//		//根据js_code获取用户信息
//		UserDetails userDetails = wxUserDetailsService.loadUserByUsername(js_code);
//		String username = userDetails.getUsername();
//		//明文密码：用户名md5，加密密码：SCryptPasswordEncoder对MD5后的值加密
//		String password = DigestUtils.md5Hex(username);
//		
//		Map<String, String> requestParameters = Maps.newLinkedHashMap();
//		requestParameters.put(USERNAME, username);
//		requestParameters.put(PASSWORD, password);
//		requestParameters.put(CLIENT_ID, client_id);
//		requestParameters.put(CLIENT_SECRET, client_secret);
//		requestParameters.put(GRANT_TYPE, grant_type);
//		
//		OAuth2Request storedRequest = new OAuth2Request(requestParameters, client_id, Sets.newHashSet(), true, SCOPE,
//				null, null, Sets.newHashSet(), Maps.newHashMap());
//
//		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password);
//		OAuth2Authentication auth2Authentication = new OAuth2Authentication(storedRequest, authentication);
//		
//		//生成token
//		OAuth2AccessToken auth2AccessToken = tokenServices.createAccessToken(auth2Authentication);
//		OAuth2RefreshToken auth2RefreshToken = auth2AccessToken.getRefreshToken();
//		
//		String accessToken = auth2AccessToken.getValue();
//		String refreshToken = auth2RefreshToken.getValue();
//		
//		Map<String, Object> tokenMap = (Map<String, Object>) accessTokenConverter.convertAccessToken(auth2AccessToken, auth2Authentication);
//		tokenMap.put(ACCESS_TOKEN, accessToken);
//		tokenMap.put(REFRESH_TOKEN, refreshToken);
//		
//		return tokenMap;
//	}
//	
//}
