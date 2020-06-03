package com.yozosoft.fileserver.service.sourcefile.impl;

import com.yozosoft.common.exception.YozoServiceException;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.model.po.FileRefRelationPo;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import com.yozosoft.fileserver.service.refrelation.IRefRelationService;
import com.yozosoft.fileserver.service.sourcefile.ISourceFileService;
import com.yozosoft.fileserver.service.storage.IStorageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-22 16:02
 **/
@Service("sourceFileServiceImpl")
@Slf4j
public class SourceFileServiceImpl implements ISourceFileService {

    @Autowired
    private IRefRelationService iRefRelationService;

    @Autowired
    private IFileRefService iFileRefService;

    @Autowired
    private IStorageManager iStorageManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IResult<String> checkAndDeleteFile(List<YozoFileRefPo> yozoFileRefPos, List<Long> fileRefIds, Integer appId) {
        //TODO 这种删除要不要判断
        Boolean relationDelete = iRefRelationService.deleteRefRelation(fileRefIds, appId);
        for (Long fileRefId : fileRefIds) {
            List<FileRefRelationPo> fileRefRelationPos = iRefRelationService.selectByQuery(fileRefId, null);
            if (fileRefRelationPos == null || fileRefRelationPos.isEmpty()) {
                //为空表示没有关联了
                iFileRefService.deleteByIds(Arrays.asList(fileRefId));
                String storageUrl = getStorageUrl(yozoFileRefPos, fileRefId);
                IResult<String> deleteResult = iStorageManager.deleteFile(storageUrl);
                if (!deleteResult.isSuccess()) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DefaultResult.failResult(deleteResult.getMessage());
                }
            }
        }
        return DefaultResult.successResult();
    }

    private String getStorageUrl(List<YozoFileRefPo> yozoFileRefPos, Long fileRefId) {
        for (YozoFileRefPo yozoFileRefPo : yozoFileRefPos) {
            if (fileRefId.equals(yozoFileRefPo.getId())) {
                return yozoFileRefPo.getStorageUrl();
            }
        }
        return null;
    }
}
