package com.yozosoft.fileserver.service.callback;

import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.dto.YozoFileRefDto;

import java.util.Map;

/**
 * @author zhoufeng
 * @description 回调service接口类
 * @create 2020-05-12 19:27
 **/
public interface ICallBackService {

    /**
     * 根据app回调
     *
     * @param appName
     * @return
     */
    IResult<Map<String, Object>> sendCallBackUrlByApp(String appName, YozoFileRefDto yozoFileRefDto);
}
