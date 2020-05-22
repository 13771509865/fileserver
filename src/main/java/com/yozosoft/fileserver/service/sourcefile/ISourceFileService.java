package com.yozosoft.fileserver.service.sourcefile;

import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;

import java.util.List;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-22 16:01
 **/
public interface ISourceFileService {

    IResult<String> checkAndDeleteFile(List<YozoFileRefPo> yozoFileRefPos, List<Long> fileRefIds, Integer appId);
}
