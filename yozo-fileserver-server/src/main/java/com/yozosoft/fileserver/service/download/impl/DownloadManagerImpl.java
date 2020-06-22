package com.yozosoft.fileserver.service.download.impl;

import com.yozosoft.fileserver.common.utils.AppUtils;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.dto.FileInfoDto;
import com.yozosoft.fileserver.dto.ServerDownloadDto;
import com.yozosoft.fileserver.dto.UserDownloadDto;
import com.yozosoft.fileserver.model.dto.FileRefInfoDto;
import com.yozosoft.fileserver.model.dto.LocalDownloadDto;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.download.IDownloadManager;
import com.yozosoft.fileserver.service.download.IDownloadService;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import com.yozosoft.fileserver.service.redis.RedisService;
import com.yozosoft.fileserver.service.storage.IStorageManager;
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
    private IStorageManager iStorageManager;

    @Autowired
    private IDownloadService iDownloadService;

    @Autowired
    private RedisService<LocalDownloadDto> redisService;

    @Autowired
    private IFileRefService iFileRefService;

    @Override
    public IResult<Map<Long, String>> serverDownload(ServerDownloadDto serverDownloadDto) {
        IResult<List<FileRefInfoDto>> checkResult = checkAndGetStorageUrls(serverDownloadDto.getAppName(), serverDownloadDto.getFileInfos());
        if (!checkResult.isSuccess()) {
            return DefaultResult.failResult(checkResult.getMessage());
        }
        String storageDir = iDownloadService.buildStorageDir(serverDownloadDto);
        IResult<Map<Long, String>> downloadResult = iStorageManager.downloadFileToServer(checkResult.getData(), storageDir);
        return downloadResult;
    }

    @Override
    public IResult<String> getDownloadUrl(UserDownloadDto userDownloadDto) {
        IResult<List<FileRefInfoDto>> checkResult = checkAndGetStorageUrls(userDownloadDto.getAppName(), userDownloadDto.getFileInfos());
        if (!checkResult.isSuccess()) {
            return DefaultResult.failResult(checkResult.getMessage());
        }
        Boolean needZip = userDownloadDto.getNeedZip() == null ? false : userDownloadDto.getNeedZip();
        return iStorageManager.generateDownloadUrl(checkResult.getData(), userDownloadDto.getZipFileName(), iDownloadService.buildTimeOut(userDownloadDto.getTimeOut()), needZip);
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

    private IResult<List<FileRefInfoDto>> checkAndGetStorageUrls(String appName, List<FileInfoDto> fileInfos) {
        IResult<Integer> checkAppResult = AppUtils.checkAppByName(appName);
        if (!checkAppResult.isSuccess()) {
            return DefaultResult.failResult(checkAppResult.getMessage());
        }
        List<Long> fileRefIds = buildFileRefIds(fileInfos);
        IResult<List<YozoFileRefPo>> buildResult = iFileRefService.buildStorageUrls(fileRefIds, checkAppResult.getData());
        if (!buildResult.isSuccess()) {
            return DefaultResult.failResult(buildResult.getMessage());
        }
        List<FileRefInfoDto> fileRefInfos = iDownloadService.buildFileRefInfos(buildResult.getData(), fileInfos);
        return DefaultResult.successResult(fileRefInfos);
    }

    private List<Long> buildFileRefIds(List<FileInfoDto> fileInfos) {
        List<Long> fileRefIds = new ArrayList<>();
        for (FileInfoDto fileInfoDto : fileInfos) {
            fileRefIds.add(fileInfoDto.getFileRefId());
        }
        return fileRefIds;
    }
}
