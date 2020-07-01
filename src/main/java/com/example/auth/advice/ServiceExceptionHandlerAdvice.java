package com.example.auth.advice;


import com.example.auth.exception.BusinessException;
import com.example.auth.message.ResponseMsg;
import com.example.auth.message.ServerMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ServiceExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public final String handleUnknownExceptions(Exception ex) {
        ResponseMsg error = new ResponseMsg(
                ServerMsg.UNKNOWN_EXCEPTION
        );
        log.info("获取到异常:{},响应为：{}", ex.getClass().getName(),error.toJson());
        ex.printStackTrace();
        return error.toJson();
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public final String handleAuthenticatorException(BusinessException ex) {
        ResponseMsg error = new ResponseMsg(ex.getCode(), ex.getMsg());
        log.info("获取到异常:{},message:{},响应为：{}", ex.getClass().getName(),ex.getMessage(),error.toJson());
        return error.toJson();
    }
}
