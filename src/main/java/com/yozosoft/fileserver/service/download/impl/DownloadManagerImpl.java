package com.yozosoft.fileserver.service.download.impl;

import com.yozosoft.fileserver.common.constants.EnumAppType;
import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.dto.*;
import com.yozosoft.fileserver.service.download.IDownloadManager;
import com.yozosoft.fileserver.service.download.IDownloadService;
import com.yozosoft.fileserver.service.redis.RedisService;
import com.yozosoft.fileserver.service.storage.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-14 16:59
 **/
@Service("downloadManagerImpl")
public class DownloadManagerImpl implements IDownloadManager {

    @Autowired
    private IStorageService iStorageService;

    @Autowired
    private IDownloadService iDownloadService;

    @Autowired
    private RedisService<LocalDownloadDto> redisService;

    @Override
    public IResult<Map<Long, String>> serverDownload(ServerDownloadDto serverDownloadDto) {
        IResult<Map<Long, FileRefInfoDto>> checkResult = checkAndGetStorageUrls(serverDownloadDto.getAppName(), serverDownloadDto.getFileInfos());
        if (!checkResult.isSuccess()) {
            return DefaultResult.failResult(checkResult.getMessage());
        }
        String storageDir = iDownloadService.buildStorageDir(serverDownloadDto);
        IResult<Map<Long, String>> downloadResult = iStorageService.downloadFileToServer(checkResult.getData(), storageDir);
        return downloadResult;
    }

    @Override
    public IResult<String> getDownloadUrl(UserDownloadDto userDownloadDto) {
        IResult<Map<Long, FileRefInfoDto>> checkResult = checkAndGetStorageUrls(userDownloadDto.getAppName(), userDownloadDto.getFileInfos());
        if (!checkResult.isSuccess()) {
            return DefaultResult.failResult(checkResult.getMessage());
        }
        return iStorageService.generateDownloadUrl(checkResult.getData(), userDownloadDto.getFileName(), iDownloadService.buildTimeOut(userDownloadDto.getTimeOut()));
    }

    @Override
    public IResult<LocalDownloadDto> getLocalDownloadDto(String downloadId) {
        try {
            LocalDownloadDto localDownloadDto = redisService.get(downloadId);
            if (localDownloadDto == null) {
                return DefaultResult.failResult(EnumResultCode.E_DOWNLOAD_URL_EXPIRE.getInfo());
            }
            FileSystemResource fileSystemResource = new FileSystemResource(localDownloadDto.getStoragePath());
            localDownloadDto.setFileSystemResource(fileSystemResource);
            localDownloadDto.setFileSize(fileSystemResource.contentLength());
            return DefaultResult.successResult(localDownloadDto);
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultResult.failResult(EnumResultCode.E_DOWNLOAD_FILE_FAIL.getInfo());
        }
    }

    private IResult<Map<Long, FileRefInfoDto>> checkAndGetStorageUrls(String appName, List<FileInfoDto> fileInfos) {
        IResult<Integer> checkAppResult = EnumAppType.checkAppByName(appName);
        if (!checkAppResult.isSuccess()) {
            return DefaultResult.failResult(checkAppResult.getMessage());
        }
        List<Long> fileRefIds = buildFileRefIds(fileInfos);
        IResult<Map<Long, String>> mapResult = iDownloadService.buildStorageUrlsMap(fileRefIds, checkAppResult.getData());
        if (!mapResult.isSuccess()) {
            return DefaultResult.failResult(mapResult.getMessage());
        }
        Map<Long, FileRefInfoDto> fileRefInfoDtoMap = iDownloadService.buildFileRefInfoMap(mapResult.getData(), fileInfos);
        return DefaultResult.successResult(fileRefInfoDtoMap);
    }

    private List<Long> buildFileRefIds(List<FileInfoDto> fileInfos) {
        List<Long> fileRefIds = new ArrayList<>();
        for (FileInfoDto fileInfoDto : fileInfos) {
            fileRefIds.add(fileInfoDto.getFileRefId());
        }
        return fileRefIds;
    }
}
