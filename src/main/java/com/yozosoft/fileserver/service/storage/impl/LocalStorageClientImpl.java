package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.constants.InterfaceConstant;
import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.UUIDHelper;
import com.yozosoft.fileserver.config.FileServerProperties;
import com.yozosoft.fileserver.config.StorageProperties;
import com.yozosoft.fileserver.model.dto.LocalDownloadDto;
import com.yozosoft.fileserver.service.redis.RedisService;
import com.yozosoft.fileserver.service.storage.IStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-21 11:37
 **/
@ConditionalOnProperty(value = "yfs.storage.type", havingValue = StorageConstant.LOCAL_STORAGE_TYPE_ID)
@Service("localStorageClientImpl")
@Slf4j
public class LocalStorageClientImpl implements IStorageClient {

    @Autowired
    private StorageProperties storageProperties;

    @Autowired
    private RedisService<LocalDownloadDto> redisService;

    @Autowired
    private FileServerProperties fileServerProperties;

    @Override
    public IResult<String> uploadFile(File file, String storageUrl, Map<String, Object> userMetadata) {
        String localRootPath = storageProperties.getLocalRootPath();
        if (StringUtils.isBlank(localRootPath)) {
            return DefaultResult.failResult(EnumResultCode.E_LOCAL_STORAGE_ROOT_ERROR.getInfo());
        }
        try {
            File storageFile = new File(localRootPath, storageUrl);
            if (storageFile.exists()) {
                storageFile.delete();
            }
            FileUtils.copyFile(file, storageFile);
            return DefaultResult.successResult(storageFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("本地保存上传文件失败", e);
            return DefaultResult.failResult(EnumResultCode.E_LOCAL_STORAGE_FILE_FAIL.getInfo());
        }
    }

    @Override
    public IResult<String> downloadFile(String storageUrl, File targetFile) {
        IResult<File> checkResult = checkFile(storageUrl);
        if (!checkResult.isSuccess()) {
            return DefaultResult.failResult(checkResult.getMessage());
        }
        File sourceFile = checkResult.getData();
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            return DefaultResult.successResult();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("本地下载文件到指定位置失败", e);
            return DefaultResult.failResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getInfo());
        }
    }

    @Override
    public IResult<String> generateUrl(String storageUrl, String fileName, Long timeOut) {
        IResult<File> checkResult = checkFile(storageUrl);
        if (!checkResult.isSuccess()) {
            return DefaultResult.failResult(checkResult.getMessage());
        }
        File downloadFile = checkResult.getData();
        IResult<String> buildResult = buildDownloadUrl(downloadFile.getAbsolutePath(), fileName, timeOut);
        return buildResult;
    }

    @Override
    public IResult<String> deleteFile(String storageUrl) {
        IResult<File> checkResult = checkFile(storageUrl);
        if (!checkResult.isSuccess()) {
            return DefaultResult.failResult(checkResult.getMessage());
        }
        File deleteFile = checkResult.getData();
        boolean flag = FileUtils.deleteQuietly(deleteFile);
        return flag ? DefaultResult.successResult() : DefaultResult.failResult(EnumResultCode.E_DELETE_REAL_FILE_FAIL.getInfo());
    }

    private IResult<File> checkFile(String storageUrl) {
        String localRootPath = storageProperties.getLocalRootPath();
        if (StringUtils.isBlank(localRootPath)) {
            return DefaultResult.failResult(EnumResultCode.E_LOCAL_STORAGE_ROOT_ERROR.getInfo());
        }
        File downloadFile = new File(localRootPath, storageUrl);
        if (!downloadFile.isFile()) {
            return DefaultResult.failResult(EnumResultCode.E_FILE_NOT_EXIST.getInfo());
        }
        return DefaultResult.successResult(downloadFile);
    }

    private IResult<String> buildDownloadUrl(String downloadPath, String fileName, Long timeOut) {
        String downloadId = UUIDHelper.generateUUID();
        LocalDownloadDto localDownloadDto = new LocalDownloadDto(fileName, downloadPath);
        boolean setResult = redisService.set(downloadId, localDownloadDto, timeOut);
        if (!setResult) {
            return DefaultResult.failResult(EnumResultCode.E_REDIS_ERROR.getInfo());
        }
        return DefaultResult.successResult(fileServerProperties.getDownloadDomain() + InterfaceConstant.LOCAL_DOWNLOAD_INTERFACE + "/" + downloadId);
    }
}
