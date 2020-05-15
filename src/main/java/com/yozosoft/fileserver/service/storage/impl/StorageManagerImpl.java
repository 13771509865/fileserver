package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DateViewUtils;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.UUIDHelper;
import com.yozosoft.fileserver.model.po.FileRefRelationPo;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import com.yozosoft.fileserver.service.refrelation.IRefRelationService;
import com.yozosoft.fileserver.service.storage.IStorageManager;
import com.yozosoft.fileserver.service.storage.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-13 11:24
 **/
@Service("storageManagerImpl")
@Slf4j
public class StorageManagerImpl implements IStorageManager {

    @Autowired
    private IStorageService iStorageService;

    @Autowired
    private IFileRefService iFileRefService;

    @Autowired
    private IRefRelationService iRefRelationService;

    private final String folderFormat = "yyyy/MM/dd";

    @Override
    public String generateStorageUrl(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        String storageUrl = StorageConstant.DOCUMENT_PATH + StorageConstant.STORAGE_SEPARATOR + DateViewUtils.format(new Date(), folderFormat) + StorageConstant.STORAGE_SEPARATOR + UUIDHelper.generateUUID() + StorageConstant.STORAGE_SEPARATOR + StorageConstant.UNIFIED_FILENAME;
        if (StringUtils.isNotBlank(extension)) {
            storageUrl = storageUrl + "." + extension;
        }
        return storageUrl;
    }

    @Override
    public IResult<YozoFileRefPo> storageFile(MultipartFile multipartFile, String storageUrl, Map<String, Object> userMetadata, String fileMd5, Integer appId) {
        IResult<String> storageResult = iStorageService.storageFile(multipartFile, storageUrl, userMetadata);
        if (!storageResult.isSuccess()) {
            return DefaultResult.failResult(storageResult.getMessage());
        }
        YozoFileRefPo yozoFileRefPo = iFileRefService.buildYozoFileRefPo(fileMd5, storageUrl, multipartFile.getSize());
        return saveFileInfo(yozoFileRefPo, appId);
    }

    @Transactional(rollbackFor = Exception.class)
    public IResult<YozoFileRefPo> saveFileInfo(YozoFileRefPo yozoFileRefPo, Integer appId) {
        IResult<Long> insertResult = iFileRefService.insertFileRefPo(yozoFileRefPo);
        if (!insertResult.isSuccess() || insertResult.getData() == null || insertResult.getData() < 0) {
            return DefaultResult.failResult(EnumResultCode.E_DB_STORAGE_FILE_REF_FAIL.getInfo());
        }
        Long fileRefId = insertResult.getData();
        yozoFileRefPo.setId(fileRefId);
        FileRefRelationPo fileRefRelationPo = iRefRelationService.buildFileRefRelationPo(fileRefId, appId);
        Boolean relationResult = iRefRelationService.insertRefRelationPo(fileRefRelationPo);
        if (!relationResult) {
            return DefaultResult.failResult(EnumResultCode.E_FILE_APP_RELATION_SAVE_FAIL.getInfo());
        }
        return DefaultResult.successResult(yozoFileRefPo);
    }
}
