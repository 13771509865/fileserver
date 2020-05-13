package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.service.storage.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-13 11:22
 **/
@ConditionalOnProperty(value = "yfs.storage.type", havingValue = StorageConstant.HW_OBS_STORAGE_TYPE_ID)
@Service("hwObsStorageServiceImpl")
@Slf4j
public class HwObsStorageServiceImpl implements IStorageService {

    @Override
    public IResult<String> storageFile(MultipartFile multipartFile, String storageUrl, Map<String, Object> userMetadata) {
        return null;
    }
}
