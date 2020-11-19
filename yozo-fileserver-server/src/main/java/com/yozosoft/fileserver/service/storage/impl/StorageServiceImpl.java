package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DateViewUtils;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.UUIDHelper;
import com.yozosoft.fileserver.config.FileServerProperties;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.model.po.FileRefRelationPo;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import com.yozosoft.fileserver.service.refrelation.IRefRelationService;
import com.yozosoft.fileserver.service.storage.IStorageClient;
import com.yozosoft.fileserver.service.storage.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * @author zhoufeng
 * @description 本地存储实现
 * @create 2020-05-13 10:56
 **/
@Service("storageServiceImpl")
@Slf4j
public class StorageServiceImpl implements IStorageService {

    @Autowired
    private FileServerProperties fileServerProperties;

    @Autowired
    private IStorageClient iStorageClient;

    @Autowired
    private IFileRefService iFileRefService;

    @Autowired
    private IRefRelationService iRefRelationService;

    private final String folderFormat = "yyyy/MM/dd";

    @Override
    public IResult<String> storageFile(MultipartFile multipartFile, String storageUrl, Map<String, Object> userMetadata) {
        File storageTempFile = new File(fileServerProperties.getTempPath(), storageUrl);
        try {
            if (!storageTempFile.getParentFile().exists()) {
                storageTempFile.getParentFile().mkdirs();
            }
            multipartFile.transferTo(storageTempFile);
            IResult<String> uploadResult = iStorageClient.uploadFile(storageTempFile, storageUrl, userMetadata);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("本地保存上传文件失败", e);
            return DefaultResult.failResult(EnumResultCode.E_LOCAL_STORAGE_FILE_FAIL.getInfo());
        } finally {
            storageTempFile.delete();
        }
    }

    @Override
    public IResult<String> storageFile(File file, String storageUrl, Map<String, Object> userMetadata) {
        IResult<String> uploadResult = iStorageClient.uploadFile(file, storageUrl, userMetadata);
        return uploadResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IResult<YozoFileRefPo> saveFileInfo(YozoFileRefPo yozoFileRefPo, Integer appId) {
        IResult<Long> insertResult = iFileRefService.insertFileRefPo(yozoFileRefPo);
        if (!insertResult.isSuccess() || insertResult.getData() == null || insertResult.getData() < 0) {
            return DefaultResult.failResult(EnumResultCode.E_DB_STORAGE_FILE_REF_FAIL.getInfo());
        }
        Long fileRefId = insertResult.getData();
        yozoFileRefPo.setId(fileRefId);
        FileRefRelationPo fileRefRelationPo = iRefRelationService.buildFileRefRelationPo(fileRefId, appId);
        IResult<Boolean> relationResult = iRefRelationService.insertRefRelationPo(fileRefRelationPo);
        if (!relationResult.isSuccess()) {
            return DefaultResult.failResult(EnumResultCode.E_FILE_APP_RELATION_SAVE_FAIL.getInfo());
        }
        yozoFileRefPo.setIsExist(relationResult.getData());
        return DefaultResult.successResult(yozoFileRefPo);
    }

    @Override
    public String generateZipStorageUrl() {
        String storageUrl = StorageConstant.DOCUMENT_PATH + StorageConstant.STORAGE_SEPARATOR
                + StorageConstant.ZIP_PATH + StorageConstant.STORAGE_SEPARATOR
                + DateViewUtils.format(new Date(), folderFormat) + StorageConstant.STORAGE_SEPARATOR
                + UUIDHelper.generateUUID() + StorageConstant.STORAGE_SEPARATOR + StorageConstant.UNIFIED_FILENAME + ".zip";
        return storageUrl;
    }
}
