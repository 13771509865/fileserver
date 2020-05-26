package com.yozosoft.fileserver.common.helper;

import com.yozosoft.fileserver.common.constants.EnumAppType;
import com.yozosoft.fileserver.common.entity.CallBackEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoufeng
 * @description app回调地址
 * @create 2020-05-12 19:04
 **/
@Component("appCallBackHelper")
public class AppCallBackHelper {

    @Autowired
    private Environment environment;

    private Map<String, CallBackEntity> appCallBackUrlMap;

    private final String callBackUrlMark = "url";

    private final String enableMark = "enable";

    private final String appMark = "app.callback";

    private final String separator = ".";

    @PostConstruct
    public void initAppCallBackUrl() {
        appCallBackUrlMap = new HashMap<>(EnumAppType.values().length);
        for (EnumAppType app : EnumAppType.values()) {
            String appName = app.getAppName();
            String enableStr = environment.getProperty(appMark + separator + appName + separator + enableMark);
            String callBackUrl = environment.getProperty(appMark + separator + appName + separator + callBackUrlMark);
            CallBackEntity callBackEntity = buildCallBackEntity(enableStr, callBackUrl);
            appCallBackUrlMap.put(appName, callBackEntity);
        }
        System.out.println("初始化app回调地址完成");
    }

    public CallBackEntity getAppCallBackEntity(String appName) {
        if (StringUtils.isNotBlank(appName)) {
            return appCallBackUrlMap.get(appName);
        }
        return null;
    }

    private CallBackEntity buildCallBackEntity(String enableStr, String url) {
        CallBackEntity callBackEntity = new CallBackEntity();
        Boolean enable = false;
        if (StringUtils.isNotBlank(enableStr)) {
            enable = Boolean.valueOf(enableStr);
        }
        callBackEntity.setEnable(enable);
        callBackEntity.setUrl(url);
        return callBackEntity;
    }
}
