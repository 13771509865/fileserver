package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.dto.FileRefInfoDto;
import com.yozosoft.fileserver.service.storage.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-13 11:20
 **/
@ConditionalOnProperty(value = "yfs.storage.type", havingValue = StorageConstant.ALI_OSS_STORAGE_TYPE_ID)
@Service("aliOssStorageServiceImpl")
@Slf4j
public class AliOssStorageServiceImpl implements IStorageService {

    @Override
    public IResult<String> storageFile(MultipartFile multipartFile, String storageUrl, Map<String, Object> userMetadata) {
        return null;
    }

    @Override
    public IResult<Map<Long, String>> downloadFileToServer(List<FileRefInfoDto> storageUrls, String storageDir) {
        return null;
    }

    @Override
    public IResult<String> generateDownloadUrl(List<FileRefInfoDto> storageUrls, String fileName, Long timeOut) {
        return null;
    }
}
