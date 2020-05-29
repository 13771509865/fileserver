package com.yozosoft.fileserver.interceptor;

import com.yozosoft.common.exception.ForbiddenAccessException;
import com.yozosoft.fileserver.common.constants.SignConstant;
import com.yozosoft.fileserver.constants.EnumResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhoufeng
 * @description 签名验证拦截器
 * @create 2020-05-15 10:41
 **/
@Component
public class SignInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String nonce = request.getParameter(SignConstant.NONCE);
        String sign = request.getParameter(SignConstant.SIGN);
        if (StringUtils.isBlank(nonce) || StringUtils.isBlank(sign)) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        return true;
    }
}
