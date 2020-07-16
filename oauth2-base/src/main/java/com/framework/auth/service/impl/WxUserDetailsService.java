package com.framework.auth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.framework.auth.enums.SysUserTypeEnum;
import com.framework.auth.redis.RedisService;
import com.framework.auth.service.ISysUserService;
import com.framework.auth.user.MyUserDetails;
import com.framework.auth.user.SysUser;

import lombok.extern.log4j.Log4j2;

/**
 * 根据js_code调用微信登陆接口获取openid和session_key相关信息
 * 
 * @author aLiang
 * @date 2020年4月2日
 */
@Log4j2
@Service
public class WxUserDetailsService implements UserDetailsService {
	
	private String USER_OPENID_SESSION_KEY = "user_openid_session_key_";
	
	@Value("${oauth.weixin.small-program.auth-url}")
	private String authUrl;
	
	@Autowired
	private ISysUserService sysUserService;
	
	@Autowired
	private RedisService<String, String> redisService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String jsCode) throws UsernameNotFoundException {
		String url = authUrl + jsCode;
		// 调用微信api需自己实现
//		String wxResult = HttpClientUtil.get(url);
		String wxResult = "{\"openid\": \"12345678\", \"session_key\":\"asfrrsdgdfgf\"}";
		log.info("调用微信地址: {} , 返回结果: {}", url, wxResult);
		if (StringUtils.isEmpty(wxResult)) {
			log.error("调用微信地址: {} , 返回结果为空", url);
			throw new UsernameNotFoundException("登录出错，调用微信接口返回数据为空");
		}
		
		JSONObject wxJson = JSONObject.parseObject(wxResult);
		String openid = wxJson.getString("openid");
		if (StringUtils.isEmpty(openid)) {
			log.error("登陆出错:无openid信息, 调用微信地址: {} , 返回结果: {}", url, wxResult);
			throw new UsernameNotFoundException("登录出错，未查询到openid信息");
		}
		
		SysUser one = sysUserService.getByOpenid(openid);
		if (one == null) {
			one = new SysUser();
			String username = UUID.randomUUID().toString().replace("-", "");
			one.setType(SysUserTypeEnum.SMALL_PROGRAM.getCode());
			one.setUsername(username);
			//用户名MD5然后再加密作为密码
			one.setPassword(passwordEncoder.encode(DigestUtils.md5Hex(username)));
			one.setOpenid(openid);
			sysUserService.save(one);
		}
		// session_key可能会在别的地方用到
		String sessionKey = wxJson.getString("session_key");
		redisService.set(USER_OPENID_SESSION_KEY + one.getId() + "_" + openid, sessionKey);
		
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		return new MyUserDetails(one, authorities);
	}
}
