package com.yozosoft.fileserver.common.helper;

import com.yozosoft.fileserver.common.utils.UrlEncodingUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author zhoufeng
 * @description httpapi aop
 * @create 2019-06-14 13:13
 **/
@Aspect
@Component
public class HttpApiServiceAspect {

    @Pointcut(value = "execution(public * com.yozosoft.fileserver.common.helper.HttpApiHelper.*(..))")
    public void httpApiMethod() {
    }

    @Around(value = "httpApiMethod()")
    public Object httpApiMethodAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        //url进行encode编码,不然httpClient可能请求失败
        Integer urlIndex = getUrlIndex(joinPoint);
        if (urlIndex > -1) {
            args[urlIndex] = UrlEncodingUtils.encodeUrl(args[urlIndex].toString());
        }
        return joinPoint.proceed(args);
    }

    /**
     * 获取方法中url
     */
    private Integer getUrlIndex(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        int urlIndex = ArrayUtils.indexOf(parameterNames, "url");
        return urlIndex;
    }
}
