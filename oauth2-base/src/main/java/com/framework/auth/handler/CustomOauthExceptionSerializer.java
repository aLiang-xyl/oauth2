package com.framework.auth.handler;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


/**
 * 登录异常-自定义处理
 * @author aLiang
 * @date 2020年4月2日
 */
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        //HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        gen.writeStartObject();
        gen.writeStringField("code", String.valueOf(value.getHttpErrorCode()));
        gen.writeStringField("msg", value.getMessage());//value.getMessage()
        gen.writeStringField("data", null);
        //gen.writeStringField("path", request.getServletPath());
        //gen.writeStringField("timestamp", String.valueOf(new Date().getTime()));
        if (value.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                gen.writeStringField(key, add);
            }
        }
        gen.writeEndObject();
    }
}
