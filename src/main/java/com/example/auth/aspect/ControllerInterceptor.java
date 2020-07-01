package com.example.auth.aspect;


import com.example.auth.utils.UuidUtils;
import com.example.auth.constant.ConfigConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class ControllerInterceptor {
    @Pointcut("execution(public * com.example.auth.controller.*.*(..))")
    public void controllerLog() {
    }

    @Before("controllerLog()")
    public void logBefore() {
        log.debug("before Controller handling:");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        String logId;
        if (request != null && request.getHeader("logId")!= null) {
            logId = request.getHeader("logId");
        } else {
            logId = UuidUtils.getUUID();
        }
        MDC.put("logId", logId);
        MDC.put("serviceName", ConfigConstants.SERVICE_NAME);
        log.debug("before Controller handling end.");
    }

    @AfterReturning("controllerLog()")
    public void logAfter() {
        log.debug("after Controller handling:");
        MDC.remove("logId");
        MDC.remove("serviceName");
        log.debug("after Controller handling end.");
    }

}
