package com.yozosoft.fileserver.common.helper;

import com.yozosoft.fileserver.config.FileServerProperties;
import com.yozosoft.fileserver.utils.FileServerVerifyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-29 08:33
 **/
@Component("signHelper")
@Slf4j
public class SignHelper {

    @Autowired
    private FileServerProperties fileServerProperties;

    public Boolean checkSign(Object params, String nonce, String sign) {
        String signSecret = fileServerProperties.getSignSecret();
        if (StringUtils.isBlank(sign) || StringUtils.isBlank(nonce) || params == null || StringUtils.isBlank(signSecret)) {
            return false;
        }
        try {
            Boolean result = FileServerVerifyUtil.verifyJsonSign(params, nonce, signSecret, sign);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("验证签名失败");
            return false;
        }
    }
}
