package com.framework.auth.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;


/**
 * @author aLiang
 * @date 2020年4月2日
 */
@SuppressWarnings("rawtypes")
@Component
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
    	int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    	if (e instanceof OAuth2Exception) {
    		status = HttpStatus.BAD_REQUEST.value();
    	} else if (e instanceof AuthenticationException) {
    		status = HttpStatus.UNAUTHORIZED.value();
    	}
        return ResponseEntity
                .status(status)
                .body(new CustomOauthException(e.getMessage()));
    }
}
