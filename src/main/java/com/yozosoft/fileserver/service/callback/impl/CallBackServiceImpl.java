package com.yozosoft.fileserver.service.callback.impl;

import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.entity.CallBackEntity;
import com.yozosoft.fileserver.common.entity.HttpResultEntity;
import com.yozosoft.fileserver.common.helper.AppCallBackHelper;
import com.yozosoft.fileserver.common.helper.HttpApiHelper;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.FastJsonUtils;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.dto.YozoFileRefDto;
import com.yozosoft.fileserver.service.callback.ICallBackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoufeng
 * @description 回调service实现类
 * @create 2020-05-12 19:28
 **/
@Service("callBackServiceImpl")
public class CallBackServiceImpl implements ICallBackService {

    @Autowired
    private AppCallBackHelper appCallBackHelper;

    @Autowired
    private HttpApiHelper httpApiHelper;

    @Override
    public IResult<Map<String, Object>> sendCallBackUrlByApp(String appName, YozoFileRefDto yozoFileRefDto) {
        Map<String, Object> resultMap = new HashMap<>();
        CallBackEntity appCallBackEntity = appCallBackHelper.getAppCallBackEntity(appName);
        if (appCallBackEntity != null && appCallBackEntity.getEnable()) {
            String appCallBackUrl = appCallBackEntity.getUrl();
            if (StringUtils.isBlank(appCallBackUrl)) {
                return DefaultResult.failResult(EnumResultCode.E_APP_CALLBACK_URL_ILLEGAL.getInfo());
            }
            IResult<HttpResultEntity> callBackResult = httpApiHelper.doPost(appCallBackUrl, FastJsonUtils.parseJSON2Map(yozoFileRefDto));
            if (!httpApiHelper.isHttpSuccess(callBackResult)) {
                return DefaultResult.failResult(EnumResultCode.E_APP_CALLBACK_FAIL.getInfo());
            }
            HttpResultEntity httpResultEntity = callBackResult.getData();
            resultMap = FastJsonUtils.parseJSON2Map(httpResultEntity);
        }
        return DefaultResult.successResult(resultMap);
    }
}
