package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.config.StorageProperties;
import com.yozosoft.fileserver.service.storage.IStorageClient;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-06-15 16:41
 **/
@ConditionalOnProperty(value = "yfs.storage.type", havingValue = StorageConstant.MINIO_STORAGE_TYPE_ID)
@Service("minioStorageClientImpl")
@Slf4j
public class MinioStorageClientImpl implements IStorageClient {

    @Autowired
    private StorageProperties storageProperties;

    private MinioClient minioClient;

    @PostConstruct
    public void init(){
        try{
            minioClient = new MinioClient(storageProperties.getEndPoint(), storageProperties.getAccesskey(), storageProperties.getSecretKey());
            System.out.println("minioClient初始化成功");
            boolean exists = minioClient.bucketExists(storageProperties.getBucketName());
            if(!exists){
                minioClient.makeBucket(storageProperties.getBucketName());
            }
            System.out.println("bucket初始化成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("初始化minioClient或bucket失败",e);
            System.out.println("初始化minioClient或bucket失败");
        }
    }

    @Override
    public IResult<String> uploadFile(File file, String storageUrl, Map<String, Object> userMetadata) {
        return null;
    }

    @Override
    public IResult<String> downloadFile(String storageUrl, File targetFile) {
        return null;
    }

    @Override
    public IResult<String> generateUrl(String storageUrl, String fileName, Long timeOut) {
        return null;
    }

    @Override
    public IResult<String> deleteFile(String storageUrl) {
        return null;
    }
}
