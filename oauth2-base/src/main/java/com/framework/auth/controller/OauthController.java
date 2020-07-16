package com.framework.auth.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.framework.auth.user.SysUser;
import com.nimbusds.jwt.SignedJWT;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * <p>
 * 鉴权相关
 * </p>
 * 
 * @author aLiang
 * @since 2020-04-23
 */
@Log4j2
@RestController
public class OauthController {
	
	/**
	 * 根据token转换用户信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "access token to user info")
	@GetMapping("userInfo")
	public SysUser userInfo(HttpServletRequest request) {
		try {
			String accessToken = request.getHeader("Authorization");
			SignedJWT jwt = SignedJWT.parse(accessToken.replace("Bearer ", ""));
			String userJson = jwt.getPayload().toJSONObject().getAsString("user");
			return JSONObject.parseObject(userJson, SysUser.class);
		} catch (ParseException e) {
			log.error("token解析失败");
			throw new UnauthorizedUserException("token解析失败", e);
		}
	}
}
