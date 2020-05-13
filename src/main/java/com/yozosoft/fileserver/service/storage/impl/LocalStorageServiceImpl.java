package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.config.StorageProperties;
import com.yozosoft.fileserver.service.storage.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
}
