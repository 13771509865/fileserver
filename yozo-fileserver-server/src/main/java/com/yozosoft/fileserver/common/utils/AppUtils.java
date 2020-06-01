package com.yozosoft.fileserver.common.utils;

import com.yozosoft.fileserver.constants.EnumAppType;
import com.yozosoft.fileserver.constants.EnumResultCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhoufeng
 * @description
 * @create 2020-06-01 15:37
 **/
public class AppUtils {

    public static IResult<Integer> checkAppByName(String appName) {
        if (StringUtils.isNotBlank(appName)) {
            EnumAppType enumAppType = EnumAppType.getEnum(appName);
            if (enumAppType != null) {
                return DefaultResult.successResult(enumAppType.getAppId());
            }
        }
        return DefaultResult.failResult(EnumResultCode.E_APP_ID_ILLEGAL.getInfo());
    }
}
