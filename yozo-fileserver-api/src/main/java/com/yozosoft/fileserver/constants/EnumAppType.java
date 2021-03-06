package com.yozosoft.fileserver.constants;

import lombok.Getter;

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
    E_YZCLOUD_WEB(0, "yzcloud", "优云web端"),
    E_TEST_APP(1, "test", "测试应用");

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
}
