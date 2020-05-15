package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.constants.InterfaceConstant;
import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.UUIDHelper;
import com.yozosoft.fileserver.common.utils.ZipUtils;
import com.yozosoft.fileserver.config.FileServerProperties;
import com.yozosoft.fileserver.config.StorageProperties;
import com.yozosoft.fileserver.model.dto.FileRefInfoDto;
import com.yozosoft.fileserver.model.dto.LocalDownloadDto;
import com.yozosoft.fileserver.service.download.IDownloadService;
import com.yozosoft.fileserver.service.redis.RedisService;
import com.yozosoft.fileserver.service.storage.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoufeng
 * @description 本地存储实现
 * @create 2020-05-13 10:56
 **/
@ConditionalOnProperty(value = "yfs.storage.type", havingValue = StorageConstant.LOCAL_STORAGE_TYPE_ID)
@Service("localStorageServiceImpl")
@Slf4j
public class LocalStorageServiceImpl implements IStorageService {

    @Autowired
    private StorageProperties storageProperties;

    @Autowired
    private FileServerProperties fileServerProperties;

    @Autowired
    private IDownloadService iDownloadService;

    @Autowired
    private RedisService<LocalDownloadDto> redisService;

    @Override
    public IResult<String> storageFile(MultipartFile multipartFile, String storageUrl, Map<String, Object> userMetadata) {
        String localRootPath = storageProperties.getLocalRootPath();
        if (StringUtils.isBlank(localRootPath)) {
            return DefaultResult.failResult(EnumResultCode.E_LOCAL_STORAGE_ROOT_ERROR.getInfo());
        }
        try {
            File storageFile = new File(localRootPath, storageUrl);
            if (storageFile.exists()) {
                storageFile.delete();
            }
            if (!storageFile.getParentFile().exists()) {
                storageFile.getParentFile().mkdirs();
            }
            multipartFile.transferTo(storageFile);
            return DefaultResult.successResult();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("本地保存上传文件失败", e);
            return DefaultResult.failResult(EnumResultCode.E_LOCAL_STORAGE_FILE_FAIL.getInfo());
        }
    }

    @Override
    public IResult<Map<Long, String>> downloadFileToServer(Map<Long, FileRefInfoDto> storageUrls, String storageDir) {
        Map<Long, String> result = new HashMap<>(storageUrls.size());
        String localRootPath = storageProperties.getLocalRootPath();
        if (StringUtils.isBlank(localRootPath)) {
            return DefaultResult.failResult(EnumResultCode.E_LOCAL_STORAGE_ROOT_ERROR.getInfo());
        }
        try {
            for (Long fileRefId : storageUrls.keySet()) {
                FileRefInfoDto fileRefInfoDto = storageUrls.get(fileRefId);
                String storageUrl = fileRefInfoDto.getStorageUrl();
                File sourceFile = new File(localRootPath, storageUrl);
                if (!sourceFile.isFile()) {
                    return DefaultResult.failResult(EnumResultCode.E_DOWNLOAD_FILE_NOT_EXIST.getInfo());
                }
                String targetFileName = iDownloadService.getTargetFileName(fileRefInfoDto.getFileName(), storageUrl);
                File targetFile = new File(storageDir, targetFileName);
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                FileUtils.copyFile(sourceFile, targetFile);
                result.put(fileRefId, targetFile.getAbsolutePath());
            }
            return DefaultResult.successResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("下载文件到服务器指定位置失败", e);
            return DefaultResult.failResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getInfo());
        }
    }

    @Override
    public IResult<String> generateDownloadUrl(Map<Long, FileRefInfoDto> storageUrls, String fileName, Long timeOut) {
        String localRootPath = storageProperties.getLocalRootPath();
        if (StringUtils.isBlank(localRootPath)) {
            return DefaultResult.failResult(EnumResultCode.E_LOCAL_STORAGE_ROOT_ERROR.getInfo());
        }
        if (storageUrls.size() == 1) {
            //单文件下载
            String storageUrl = null;
            //TODO 改造
            for (FileRefInfoDto fileRefInfoDto : storageUrls.values()) {
                storageUrl = fileRefInfoDto.getStorageUrl();
            }
            File downloadFile = new File(localRootPath, storageUrl);
            if (!downloadFile.isFile()) {
                return DefaultResult.failResult(EnumResultCode.E_DOWNLOAD_FILE_NOT_EXIST.getInfo());
            }
            IResult<String> buildResult = buildDownloadUrl(downloadFile.getAbsolutePath(), fileName, timeOut);
            if (!buildResult.isSuccess()) {
                return DefaultResult.failResult(buildResult.getMessage());
            }
            return DefaultResult.successResult(buildResult.getData());
        } else {
            //多文件下载
            File zipDir = iDownloadService.buildZipDir();
            File zipFile = iDownloadService.buildZipFile(zipDir, fileName);
            IResult<Map<Long, String>> downloadResult = downloadFileToServer(storageUrls, zipDir.getAbsolutePath());
            if (!downloadResult.isSuccess()) {
                return DefaultResult.failResult(downloadResult.getMessage());
            }
            IResult<String> zipResult = ZipUtils.zipFile(zipFile, zipDir);
            if (!zipResult.isSuccess()) {
                return DefaultResult.failResult(zipResult.getMessage());
            }
            IResult<String> buildResult = buildDownloadUrl(zipFile.getAbsolutePath(), fileName, timeOut);
            if (!buildResult.isSuccess()) {
                return DefaultResult.failResult(buildResult.getMessage());
            }
            return DefaultResult.successResult(buildResult.getData());
        }
    }

    private IResult<String> buildDownloadUrl(String downloadPath, String fileName, Long timeOut) {
        String downloadId = UUIDHelper.generateUUID();
        LocalDownloadDto localDownloadDto = new LocalDownloadDto(fileName, downloadPath);
        boolean setResult = redisService.set(downloadId, localDownloadDto, iDownloadService.buildTimeOut(timeOut));
        if (!setResult) {
            return DefaultResult.failResult(EnumResultCode.E_REDIS_ERROR.getInfo());
        }
        return DefaultResult.successResult(fileServerProperties.getDownloadDomain() + InterfaceConstant.LOCAL_DOWNLOAD_INTERFACE + "/" + downloadId);
    }
}
