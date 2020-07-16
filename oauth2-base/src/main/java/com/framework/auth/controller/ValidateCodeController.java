package com.framework.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.framework.auth.validate.ValidateCodeUtils;

import lombok.extern.log4j.Log4j2;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 验证码前端控制器
 * </p>
 * 
 * @author aLiang
 * @since 2020-04-02
 */
@Log4j2
@RestController
public class ValidateCodeController {
	@Autowired
	ValidateCodeUtils validateCodeUtils;
	
	@ApiIgnore
	@GetMapping(value = { "/free/getVerifyImg" })
	public void getVerifyImg(HttpServletRequest request, HttpServletResponse response, String uuid) {
		try {
			response.setContentType("image/jpeg");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			validateCodeUtils.getRandcode(request, response);
		} catch (Exception e) {
			log.error("获取验证码失败>>>>   ", e);
		}
	}
}
