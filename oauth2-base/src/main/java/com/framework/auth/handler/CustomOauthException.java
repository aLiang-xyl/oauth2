package com.framework.auth.handler;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;


/**
 * @author aLiang
 * @date 2020年4月2日
 */
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomOauthException(String msg) {
        super(msg);
    }
}
