package com.yozosoft.fileserver.interceptor;

import com.yozosoft.common.exception.ForbiddenAccessException;
import com.yozosoft.fileserver.common.constants.SignConstant;
import com.yozosoft.fileserver.config.FileServerProperties;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.utils.FileServerVerifyUtil;
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
        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.putAll(request.getParameterMap());
        String[] nonceStr = parameterMap.get(SignConstant.NONCE);
        String[] signStr = parameterMap.get(SignConstant.SIGN);
        if (nonceStr == null || signStr == null || nonceStr.length < 1 || signStr.length < 1) {
            String nonceHeader = request.getHeader(SignConstant.NONCE);
            String signHeader = request.getHeader(SignConstant.SIGN);
            if (StringUtils.isBlank(nonceHeader) || StringUtils.isBlank(signHeader)) {
                throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
            }
            parameterMap.put(SignConstant.NONCE, new String[]{nonceHeader});
            parameterMap.put(SignConstant.SIGN, new String[]{signHeader});
        }
        Boolean verifyResult = FileServerVerifyUtil.verifySign(parameterMap, fileServerProperties.getSignSecret(), parameterMap.get(SignConstant.SIGN)[0]);
        if (!verifyResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        return true;
    }
}
