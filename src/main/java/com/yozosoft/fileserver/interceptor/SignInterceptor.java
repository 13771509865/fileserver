package com.yozosoft.fileserver.interceptor;

import com.yozosoft.common.exception.ForbiddenAccessException;
import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.constants.SignConstant;
import com.yozosoft.fileserver.common.utils.FileServerVerifyUtil;
import com.yozosoft.fileserver.config.FileServerProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoufeng
 * @description 签名验证拦截器
 * @create 2020-05-15 10:41
 **/
@Component
public class SignInterceptor implements HandlerInterceptor {

    @Autowired
    private FileServerProperties fileServerProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String nonce = request.getHeader(SignConstant.NONCE);
        String sign = request.getHeader(SignConstant.SIGN);
        if (StringUtils.isBlank(nonce) || StringUtils.isBlank(sign)) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.putAll(request.getParameterMap());
        parameterMap.put(SignConstant.NONCE, new String[]{nonce});
        Boolean verifyResult = FileServerVerifyUtil.verifySign(parameterMap, fileServerProperties.getSignSecret(), sign);
        if (!verifyResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        return true;
    }
}
