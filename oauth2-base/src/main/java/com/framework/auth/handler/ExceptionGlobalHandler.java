//package com.framework.auth.handler;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.wayto.common.util.enums.ResultEnum;
//import com.wayto.common.util.result.Result;
//
//import lombok.extern.log4j.Log4j2;
//
//
///**
// *  全局异常处理
// * @author aLiang
// * @date 2019年4月24日下午3:40:41
// */
//@Log4j2
//@ControllerAdvice
//public class ExceptionGlobalHandler {
//
//	@ResponseBody
//    @ExceptionHandler(value = Exception.class)
//    public Result<?> exceptionHandler(Exception ex) {
//		String msg = ex.getMessage();
//        log.error("捕获到全局Exception异常",ex);
//        msg = StringUtils.isBlank(msg)?ResultEnum.FAIL.getMsg():msg;
//        return new Result<>().NO(msg);
//    }
//}
