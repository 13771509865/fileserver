package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.*;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.model.dto.FileRefInfoDto;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.download.IDownloadService;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import com.yozosoft.fileserver.service.storage.IStorageClient;
import com.yozosoft.fileserver.service.storage.IStorageManager;
import com.yozosoft.fileserver.service.storage.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private IStorageClient iStorageClient;

    @Autowired
    private IDownloadService iDownloadService;

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
        return iStorageService.saveFileInfo(yozoFileRefPo, appId);
    }

    @Override
    public IResult<YozoFileRefPo> storageFile(File file, String storageUrl, Map<String, Object> userMetadata, String fileMd5, Integer appId) {
        IResult<String> storageResult = iStorageService.storageFile(file, storageUrl, userMetadata);
        if (!storageResult.isSuccess()) {
            return DefaultResult.failResult(storageResult.getMessage());
        }
        YozoFileRefPo yozoFileRefPo = iFileRefService.buildYozoFileRefPo(fileMd5, storageUrl, file.length());
        return iStorageService.saveFileInfo(yozoFileRefPo, appId);
    }

    @Override
    public IResult<String> deleteFile(String storageUrl) {
        if (StringUtils.isBlank(storageUrl)) {
            return DefaultResult.failResult(EnumResultCode.E_DELETE_REAL_FILE_FAIL.getInfo());
        }
        return iStorageClient.deleteFile(storageUrl);
    }

    @Override
    public IResult<Map<Long, String>> downloadFileToServer(List<FileRefInfoDto> storageUrls, String storageDir) {
        Map<Long, String> result = new HashMap<>(storageUrls.size());
        for (FileRefInfoDto fileRefInfoDto : storageUrls) {
            String storageUrl = fileRefInfoDto.getStorageUrl();
            String fileRelativePath = fileRefInfoDto.getFileRelativePath();
            String targetFileName = iDownloadService.getTargetFileName(fileRefInfoDto.getFileName(), storageUrl);
            File targetFile = null;
            if (StringUtils.isNotBlank(fileRelativePath)) {
                targetFile = new File(storageDir, fileRelativePath + File.separator + targetFileName);
            } else {
                targetFile = new File(storageDir, targetFileName);
            }
            if (targetFile.exists()) {
                targetFile.delete();
            }
            IResult<String> downloadResult = iStorageClient.downloadFile(storageUrl, targetFile);
            if (!downloadResult.isSuccess()) {
                return DefaultResult.failResult(downloadResult.getMessage());
            }
            result.put(fileRefInfoDto.getFileRefId(), targetFile.getAbsolutePath());
        }
        return DefaultResult.successResult(result);
    }

    @Override
    public IResult<String> generateDownloadUrl(List<FileRefInfoDto> storageUrls, String zipFileName, Long timeOut, Boolean needZip) {
        if (storageUrls.size() == 1 && !needZip) {
            //单文件下载
            FileRefInfoDto fileRefInfoDto = storageUrls.get(0);
            String storageUrl = fileRefInfoDto.getStorageUrl();
            String targetFileName = iDownloadService.getTargetFileName(fileRefInfoDto.getFileName(), storageUrl);
            IResult<String> generateResult = iStorageClient.generateUrl(storageUrl, targetFileName, timeOut);
            return generateResult;
        } else {
            //多文件下载
            File zipDir = iDownloadService.buildZipDir();
            File zipFile = iDownloadService.buildZipFile(zipDir, zipFileName);
            IResult<Map<Long, String>> downloadResult = downloadFileToServer(storageUrls, zipDir.getAbsolutePath());
            if (!downloadResult.isSuccess()) {
                return DefaultResult.failResult(downloadResult.getMessage());
            }
            IResult<String> zipResult = ZipUtils.zipFile(zipFile, zipDir);
            if (!zipResult.isSuccess()) {
                return DefaultResult.failResult(zipResult.getMessage());
            }
            String zipStorageUrl = iStorageService.generateZipStorageUrl();
            IResult<String> uploadResult = iStorageClient.uploadFile(zipFile, zipStorageUrl, null);
            if (!uploadResult.isSuccess()) {
                return DefaultResult.failResult(uploadResult.getMessage());
            }
            IResult<String> generateUrlResult = iStorageClient.generateUrl(zipStorageUrl, zipFile.getName(), timeOut);
            return generateUrlResult;
        }
    }
}
