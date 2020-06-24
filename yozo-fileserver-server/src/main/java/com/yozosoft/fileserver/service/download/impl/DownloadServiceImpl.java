package com.yozosoft.fileserver.service.download.impl;

import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.constants.TimeConstant;
import com.yozosoft.fileserver.common.utils.DateViewUtils;
import com.yozosoft.fileserver.common.utils.UUIDHelper;
import com.yozosoft.fileserver.config.FileServerProperties;
import com.yozosoft.fileserver.dto.FileInfoDto;
import com.yozosoft.fileserver.dto.ServerDownloadDto;
import com.yozosoft.fileserver.model.dto.FileRefInfoDto;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.download.IDownloadService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-14 17:21
 **/
@Service("downloadServiceImpl")
public class DownloadServiceImpl implements IDownloadService {

    @Autowired
    private FileServerProperties fileServerProperties;

    private final String folderFormat = "yyyy/MM/dd";

    @Override
    public String getTargetFileName(String fileName, String storageUrl) {
        if (StringUtils.isNotBlank(fileName)) {
            if (StringUtils.isBlank(FilenameUtils.getExtension(fileName))) {
                String extension = FilenameUtils.getExtension(storageUrl);
                if (StringUtils.isNotBlank(extension)) {
                    return FilenameUtils.getBaseName(fileName) + "." + extension;
                }
            }
            return fileName;
        }
        return FilenameUtils.getName(storageUrl);
    }

    @Override
    public String buildStorageDir(ServerDownloadDto serverDownloadDto) {
        String storageDir = serverDownloadDto.getStorageDir();
        if (StringUtils.isBlank(storageDir)) {
            storageDir = fileServerProperties.getDownloadRoot() + StorageConstant.STORAGE_SEPARATOR + serverDownloadDto.getAppName() + StorageConstant.STORAGE_SEPARATOR + DateViewUtils.format(new Date(), folderFormat)
                    + StorageConstant.STORAGE_SEPARATOR + UUIDHelper.generateUUID();
        }
        storageDir = storageDir.replaceAll("(\\\\|/)", Matcher.quoteReplacement(File.separator));
        return storageDir;
    }

    @Override
    public List<FileRefInfoDto> buildFileRefInfos(List<YozoFileRefPo> fileRefs, List<FileInfoDto> fileInfos) {
        List<FileRefInfoDto> result = new ArrayList<>();
        Map<Long, YozoFileRefPo> yozoFileRefPoMap = buildYozoFileRefPoMap(fileRefs);
        for (FileInfoDto fileInfoDto : fileInfos) {
            Long fileRefId = fileInfoDto.getFileRefId();
            String fileName = fileInfoDto.getFileName();
            String storageUrl = yozoFileRefPoMap.get(fileRefId).getStorageUrl();
            FileRefInfoDto fileRefInfoDto = new FileRefInfoDto(fileRefId, storageUrl, fileName);
            result.add(fileRefInfoDto);
        }
        return result;
    }

    @Override
    public File buildZipDir() {
        File zipDir = new File(fileServerProperties.getTempPath(), UUIDHelper.generateUUID());
        if (zipDir.exists()) {
            zipDir.delete();
        }
        zipDir.mkdirs();
        return zipDir;
    }

    @Override
    public File buildZipFile(File zipDir, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = StorageConstant.MULTIPLE_DOWNLOAD_FILENAME;
        }
        File zipFile = new File(zipDir, FilenameUtils.getBaseName(fileName) + ".zip");
        if (zipFile.exists()) {
            zipFile.delete();
        }
        return zipFile;
    }

    @Override
    public Long buildTimeOut(Long timeOut) {
        Long expireTime = (timeOut != null && timeOut > 0) ? timeOut : 30 * TimeConstant.SECOND_OF_MINUTE;
        return expireTime;
    }

    private Map<Long, YozoFileRefPo> buildYozoFileRefPoMap(List<YozoFileRefPo> fileRefs) {
        Map<Long, YozoFileRefPo> result = new HashMap<>(fileRefs.size());
        for (YozoFileRefPo yozoFileRefPo : fileRefs) {
            result.put(yozoFileRefPo.getId(), yozoFileRefPo);
        }
        return result;
    }
}
