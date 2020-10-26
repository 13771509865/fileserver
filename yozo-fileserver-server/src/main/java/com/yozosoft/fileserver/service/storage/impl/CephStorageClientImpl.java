package com.yozosoft.fileserver.service.storage.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.HttpUtils;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.StorageFileUtils;
import com.yozosoft.fileserver.config.StorageProperties;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.service.storage.IStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author zhoufeng
 * @description ceph对象存储
 * @create 2020-10-22 16:41
 **/
@ConditionalOnProperty(value = "yfs.storage.type", havingValue = StorageConstant.CEPH_STORAGE_TYPE_ID)
@Service("cephStorageClientImpl")
@Slf4j
public class CephStorageClientImpl implements IStorageClient {

    @Autowired
    private StorageProperties storageProperties;

    private AmazonS3 amazonS3;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(storageProperties.getAccesskey(), storageProperties.getSecretKey());
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(storageProperties.getEndPoint(),"")).build();
        System.out.println("CephClient初始化成功");
        boolean exists = amazonS3.doesBucketExistV2(storageProperties.getBucketName());
        if(!exists){
            amazonS3.createBucket(storageProperties.getBucketName());
        }
        System.out.println("bucket初始化成功");
    }

    @Override
    public IResult<String> uploadFile(File file, String storageUrl, Map<String, Object> userMetadata) {
        long fileSize = file.length();
        Integer partCount = StorageFileUtils.getPartCount(fileSize);
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .withMinimumUploadPartSize(StorageConstant.PART_SIZE)
                .withMultipartCopyThreshold(StorageConstant.PART_SIZE)
                .withExecutorFactory(() -> Executors.newFixedThreadPool(partCount))
                .build();
        PutObjectRequest request = new PutObjectRequest(storageProperties.getBucketName(), storageUrl, file);
        ObjectMetadata objectMetadata = buildObjectMetadata(null, userMetadata);
        request.setMetadata(objectMetadata);
        try{
            Upload upload = transferManager.upload(request);
            upload.waitForCompletion();
        }catch (Exception e){
            e.printStackTrace();
            log.error("ceph上传文件失败,storageUrl为:" + storageUrl + ",失败message为:" + e.getMessage(), e);
            return DefaultResult.failResult(EnumResultCode.E_CEPH_STORAGE_FILE_FAIL.getInfo());
        }
        return DefaultResult.successResult(storageUrl);
    }

    @Override
    public IResult<String> downloadFile(String storageUrl, File targetFile) {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .withDisableParallelDownloads(false)
                .build();
        GetObjectRequest getObjectRequest = new GetObjectRequest(storageProperties.getBucketName(), storageUrl);
        try{
            Download download = transferManager.download(getObjectRequest, targetFile);
            download.waitForCompletion();
        }catch (Exception e){
            e.printStackTrace();
            log.error("ceph下载文件失败,storageUrl为:" + storageUrl + ",失败message为:" + e.getMessage(), e);
            return DefaultResult.failResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getInfo());
        }
        return DefaultResult.successResult();
    }

    @Override
    public IResult<String> generateUrl(String storageUrl, String fileName, Long timeOut) {
        Date expiration = new Date(System.currentTimeMillis() + (timeOut * 1000));
        String encodeFileName = HttpUtils.urlEncode(fileName);
        ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
        responseHeaders.setContentDisposition("attachment; filename=\"" + encodeFileName + "\";filename*=UTF-8''" + encodeFileName);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(storageProperties.getBucketName(), storageUrl)
                .withExpiration(expiration)
                .withMethod(HttpMethod.GET)
                .withResponseHeaders(responseHeaders);
        URL url;
        try{
            url = amazonS3.generatePresignedUrl(request);
        }catch (Exception e){
            e.printStackTrace();
            log.error("生成下载链接失败,storageUrl为:" + storageUrl + ",失败message为:" + e.getMessage(), e);
            return DefaultResult.failResult(EnumResultCode.E_CEPH_GENERATE_DOWNLOAD_URL_FAIL.getInfo());
        }
        return DefaultResult.successResult(url.toString());
    }

    @Override
    public IResult<String> deleteFile(String storageUrl) {
        try{
            amazonS3.deleteObject(storageProperties.getBucketName(), storageUrl);
        }catch (Exception e){
            e.printStackTrace();
            log.error("ceph删除文件失败,storageUrl为:" + storageUrl + ",失败message为:" + e.getMessage(), e);
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
}
