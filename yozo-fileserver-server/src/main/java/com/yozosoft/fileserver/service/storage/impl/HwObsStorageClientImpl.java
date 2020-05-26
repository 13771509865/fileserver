package com.yozosoft.fileserver.service.storage.impl;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.HttpUtils;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.StorageFileUtils;
import com.yozosoft.fileserver.config.StorageProperties;
import com.yozosoft.fileserver.service.storage.IStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-25 09:33
 **/
@ConditionalOnProperty(value = "yfs.storage.type", havingValue = StorageConstant.HW_OBS_STORAGE_TYPE_ID)
@Service("hwObsStorageClientImpl")
@Slf4j
public class HwObsStorageClientImpl implements IStorageClient {

    @Autowired
    private StorageProperties storageProperties;

    private ObsClient obsClient = null;

    @PostConstruct
    public void init() {
        ObsConfiguration obsConfiguration = new ObsConfiguration();
        obsConfiguration.setEndPoint(storageProperties.getEndPoint());
        obsClient = new ObsClient(storageProperties.getAccesskey(), storageProperties.getSecretKey(), obsConfiguration);
        System.out.println("ObsClient初始化成功");
        boolean exists = obsClient.headBucket(storageProperties.getBucketName());
        if (!exists) {
            obsClient.createBucket(storageProperties.getBucketName());
        }
        System.out.println("bucket初始化成功");
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (obsClient != null) {
            obsClient.close();
        }
    }

    @Override
    public IResult<String> uploadFile(File file, String storageUrl, Map<String, Object> userMetadata) {
        long fileSize = file.length();
        Integer partCount = StorageFileUtils.getPartCount(fileSize);
        UploadFileRequest request = new UploadFileRequest(storageProperties.getBucketName(), storageUrl);
        request.setUploadFile(file.getAbsolutePath());
        //开启断点续传
        request.setEnableCheckpoint(true);
        request.setPartSize(StorageConstant.PART_SIZE);
        request.setTaskNum(partCount);
        ObjectMetadata objectMetadata = buildObjectMetadata(null, userMetadata);
        request.setObjectMetadata(objectMetadata);
        try {
            CompleteMultipartUploadResult result = obsClient.uploadFile(request);
        } catch (ObsException e) {
            e.printStackTrace();
            log.error("obs上传文件失败,storageUrl为:" + storageUrl + ",失败errorCode为:" + e.getErrorCode() + ",失败responseCode为:" + e.getResponseCode() + ",失败message为:" + e.getErrorMessage(), e);
            return DefaultResult.failResult(EnumResultCode.E_OBS_STORAGE_FILE_FAIL.getInfo());
        }
        return DefaultResult.successResult(storageUrl);
    }

    @Override
    public IResult<String> downloadFile(String storageUrl, File targetFile) {
        Integer taskNum = 1;
        ObjectMetadata objectMetadata = getObjectMetadata(storageUrl);
        if (objectMetadata != null && objectMetadata.getContentLength() != null && objectMetadata.getContentLength() > 0) {
            taskNum = StorageFileUtils.getDownloadPartCount(objectMetadata.getContentLength());
        }
        DownloadFileRequest request = new DownloadFileRequest(storageProperties.getBucketName(), storageUrl);
        request.setDownloadFile(targetFile.getAbsolutePath());
        //开启断点续传模式
        request.setEnableCheckpoint(true);
        request.setPartSize(StorageConstant.DOWNLOAD_PART_SIZE);
        request.setTaskNum(taskNum);
        try {
            DownloadFileResult result = obsClient.downloadFile(request);
        } catch (ObsException e) {
            e.printStackTrace();
            log.error("obs下载文件失败,storageUrl为:" + storageUrl + ",失败errorCode为:" + e.getErrorCode() + ",失败responseCode为:" + e.getResponseCode() + ",失败message为:" + e.getErrorMessage(), e);
            return DefaultResult.failResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getInfo());
        }
        return DefaultResult.successResult();
    }

    @Override
    public IResult<String> generateUrl(String storageUrl, String fileName, Long timeOut) {
        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, timeOut);
        request.setBucketName(storageProperties.getBucketName());
        request.setObjectKey(storageUrl);
        Map<String, Object> params = new HashMap<>(1);
        String encodeFileName = HttpUtils.urlEncode(fileName);
        params.put("response-content-disposition", "attachment; filename=\"" + encodeFileName + "\";filename*=UTF-8''" + encodeFileName);
        request.setQueryParams(params);
        TemporarySignatureResponse temporarySignature;
        try {
            temporarySignature = obsClient.createTemporarySignature(request);
        } catch (ObsException e) {
            e.printStackTrace();
            log.error("生成下载链接失败,storageUrl为:" + storageUrl + ",失败errorCode为:" + e.getErrorCode() + ",失败responseCode为:" + e.getResponseCode() + ",失败message为:" + e.getErrorMessage(), e);
            return DefaultResult.failResult(EnumResultCode.E_OBS_GENERATE_DOWNLOAD_URL_FAIL.getInfo());
        }
        return DefaultResult.successResult(temporarySignature.getSignedUrl());
    }

    @Override
    public IResult<String> deleteFile(String storageUrl) {
        try {
            DeleteObjectResult result = obsClient.deleteObject(storageProperties.getBucketName(), storageUrl);
        } catch (ObsException e) {
            e.printStackTrace();
            log.error("obs删除文件失败,storageUrl为:" + storageUrl + ",失败errorCode为:" + e.getErrorCode() + ",失败responseCode为:" + e.getResponseCode() + ",失败message为:" + e.getErrorMessage(), e);
            return DefaultResult.failResult(EnumResultCode.E_DELETE_REAL_FILE_FAIL.getInfo());
        }
        return DefaultResult.successResult();
    }

    private ObjectMetadata buildObjectMetadata(ObjectMetadata objectMetadata, Map<String, Object> userMetadata) {
        if (objectMetadata == null) {
            objectMetadata = new ObjectMetadata();
        }
        if (userMetadata != null && !userMetadata.isEmpty()) {
            for (String key : userMetadata.keySet()) {
                objectMetadata.addUserMetadata(key, userMetadata.get(key).toString());
            }
        }
        return objectMetadata;
    }

    private ObjectMetadata getObjectMetadata(String objectName) {
        try {
            ObjectMetadata objectMetadata = obsClient.getObjectMetadata(storageProperties.getBucketName(), objectName);
            return objectMetadata;
        } catch (ObsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
