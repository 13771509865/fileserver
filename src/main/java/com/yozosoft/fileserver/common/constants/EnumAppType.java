package com.yozosoft.fileserver.common.constants;

import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhoufeng
 * @description app枚举类
 * @create 2020-05-12 18:54
 **/
@Getter
public enum EnumAppType {

    /**
     * 枚举值
     */
    E_YZCLOUD_WEB(0, "yzcloud", "优云web端");

    private Integer appId;
    private String appName;
    private String appDesc;

    EnumAppType(Integer appId, String appName, String appDesc) {
        this.appId = appId;
        this.appName = appName;
        this.appDesc = appDesc;
    }

    public static EnumAppType getEnum(String appName) {
        for (EnumAppType app : values()) {
            if (app.getAppName().equals(appName)) {
                return app;
            }
        }
        return null;
    }

    public static IResult<Integer> checkAppByName(String appName) {
        if (StringUtils.isNotBlank(appName)) {
            EnumAppType enumAppType = getEnum(appName);
            if (enumAppType != null) {
                return DefaultResult.successResult(enumAppType.getAppId());
            }
        }
        return DefaultResult.failResult(EnumResultCode.E_APP_ID_ILLEGAL.getInfo());
    }
}
