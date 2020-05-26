package com.yozosoft.fileserver.service.storage.impl;

import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
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
import java.net.URL;
import java.util.Date;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-26 08:39
 **/
@ConditionalOnProperty(value = "yfs.storage.type", havingValue = StorageConstant.ALI_OSS_STORAGE_TYPE_ID)
@Service("aliOssStorageClientImpl")
@Slf4j
public class AliOssStorageClientImpl implements IStorageClient {

    @Autowired
    private StorageProperties storageProperties;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setMaxConnections(2048);
        ossClient = new OSSClientBuilder().build(storageProperties.getEndPoint(), storageProperties.getAccesskey(), storageProperties.getSecretKey(), conf);
        System.out.println("OssClient初始化成功");
        boolean exists = ossClient.doesBucketExist(storageProperties.getBucketName());
        if (!exists) {
            ossClient.createBucket(storageProperties.getBucketName());
        }
        System.out.println("bucket初始化成功");
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
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
            UploadFileResult uploadFileResult = ossClient.uploadFile(request);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error("oss上传文件失败,storageUrl为:" + storageUrl + ",失败message为:" + throwable.getMessage(), throwable);
            return DefaultResult.failResult(EnumResultCode.E_OSS_STORAGE_FILE_FAIL.getInfo());
        }
        return DefaultResult.successResult(storageUrl);
    }

    @Override
    public IResult<String> downloadFile(String storageUrl, File targetFile) {
        Integer taskNum = 1;
        ObjectMetadata objectMetadata = getObjectMetadata(storageUrl);
        if (objectMetadata != null && objectMetadata.getContentLength() > 0) {
            taskNum = StorageFileUtils.getDownloadPartCount(objectMetadata.getContentLength());
        }
        DownloadFileRequest request = new DownloadFileRequest(storageProperties.getBucketName(), storageUrl);
        request.setDownloadFile(targetFile.getAbsolutePath());
        //开启断点续传
        request.setEnableCheckpoint(true);
        request.setPartSize(StorageConstant.DOWNLOAD_PART_SIZE);
        request.setTaskNum(taskNum);
        try {
            DownloadFileResult downloadFileResult = ossClient.downloadFile(request);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error("oss下载文件失败,storageUrl为:" + storageUrl + ",失败message为:" + throwable.getMessage(), throwable);
            return DefaultResult.failResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getInfo());
        }
        return DefaultResult.successResult();
    }

    @Override
    public IResult<String> generateUrl(String storageUrl, String fileName, Long timeOut) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(storageProperties.getBucketName(), storageUrl, HttpMethod.GET);
        Date expiration = new Date(System.currentTimeMillis() + (timeOut * 1000));
        request.setExpiration(expiration);
        String encodeFileName = HttpUtils.urlEncode(fileName);
        ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
        responseHeaders.setContentDisposition("attachment; filename=\"" + encodeFileName + "\";filename*=UTF-8''" + encodeFileName);
        request.setResponseHeaders(responseHeaders);
        URL url;
        try {
            url = ossClient.generatePresignedUrl(request);
        } catch (ClientException e) {
            e.printStackTrace();
            log.error("生成下载链接失败,storageUrl为:" + storageUrl + ",失败errorCode为:" + e.getErrorCode() + ",失败message为:" + e.getErrorMessage(), e);
            return DefaultResult.failResult(EnumResultCode.E_OSS_GENERATE_DOWNLOAD_URL_FAIL.getInfo());
        }
        return DefaultResult.successResult(url.toString());
    }

    @Override
    public IResult<String> deleteFile(String storageUrl) {
        try {
            ossClient.deleteObject(storageProperties.getBucketName(), storageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("oss删除文件失败,storageUrl为:" + storageUrl + ",失败message为:" + e.getMessage(), e);
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
            ObjectMetadata objectMetadata = ossClient.getObjectMetadata(storageProperties.getBucketName(), objectName);
            return objectMetadata;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
