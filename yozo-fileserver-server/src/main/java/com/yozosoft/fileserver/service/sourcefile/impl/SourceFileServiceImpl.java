package com.yozosoft.fileserver.service.sourcefile.impl;

import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
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

import java.util.ArrayList;
import java.util.Collections;
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
        List<Long> usedFileRefIds = iRefRelationService.selectUsedFileRefIds(fileRefIds);
        List<Long> deleteFileRefIds = getDeleteFileRefIds(fileRefIds, usedFileRefIds);
        iFileRefService.deleteByIds(deleteFileRefIds);
        //删除物理文件
        for (Long fileRefId : deleteFileRefIds) {
            String storageUrl = getStorageUrl(yozoFileRefPos, fileRefId);
            IResult<String> deleteResult = iStorageManager.deleteFile(storageUrl);
            if (!deleteResult.isSuccess()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DefaultResult.failResult(deleteResult.getMessage());
            }
        }
        return DefaultResult.successResult();
    }

    private List<Long> getDeleteFileRefIds(List<Long> fileRefIds, List<Long> usedFileRefIds) {
        if (usedFileRefIds == null || usedFileRefIds.isEmpty()) {
            return fileRefIds;
        }
        //深拷贝
        List<Long> result = new ArrayList<>();
        Collections.addAll(result, new Long[fileRefIds.size()]);
        Collections.copy(result, fileRefIds);
        result.removeAll(usedFileRefIds);
        return result;
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
