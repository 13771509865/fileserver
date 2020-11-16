package com.yozosoft.fileserver.service.storage.impl;

import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.HttpUtils;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.config.StorageProperties;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.service.storage.IStorageClient;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
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
    public void init() {
        try {
            minioClient = new MinioClient(storageProperties.getEndPoint(), storageProperties.getAccesskey(), storageProperties.getSecretKey());
            System.out.println("minioClient初始化成功");
            boolean exists = minioClient.bucketExists(storageProperties.getBucketName());
            if (!exists) {
                minioClient.makeBucket(storageProperties.getBucketName());
            }
            System.out.println("bucket初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化minioClient或bucket失败", e);
            System.out.println("初始化minioClient或bucket失败");
        }
    }

    @Override
    public IResult<String> uploadFile(File file, String storageUrl, Map<String, Object> userMetadata) {
        try {
            PutObjectOptions putObjectOptions = new PutObjectOptions(file.length(), StorageConstant.PART_SIZE);
            String bucketName = storageProperties.getBucketName();
            minioClient.putObject(bucketName, storageUrl, file.getAbsolutePath(), putObjectOptions);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("minio上传文件失败,storageUrl为:" + storageUrl, e);
            return DefaultResult.failResult(EnumResultCode.E_MINIO_STORAGE_FILE_FAIL.getInfo());
        }
        return DefaultResult.successResult(storageUrl);
    }

    @Override
    public IResult<String> downloadFile(String storageUrl, File targetFile) {
        try {
            String bucketName = storageProperties.getBucketName();
            minioClient.getObject(bucketName, storageUrl, targetFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("minio下载文件失败,storageUrl为:" + storageUrl, e);
            return DefaultResult.failResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getInfo());
        }
        return DefaultResult.successResult();
    }

    @Override
    public IResult<String> generateUrl(String storageUrl, String fileName, Long timeOut) {
        try {
            Map<String, String> reqParams = new HashMap<>();
            String encodeFileName = HttpUtils.urlEncode(fileName);
            reqParams.put("response-content-disposition", "attachment; filename=\"" + encodeFileName + "\";filename*=UTF-8''" + encodeFileName);
            String url = minioClient.presignedGetObject(storageProperties.getBucketName(), storageUrl, timeOut.intValue(), reqParams);
            return DefaultResult.successResult(url);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("生成下载链接失败,storageUrl为:" + storageUrl, e);
            return DefaultResult.failResult(EnumResultCode.E_MINIO_GENERATE_DOWNLOAD_URL_FAIL.getInfo());
        }
    }

    @Override
    public IResult<String> deleteFile(String storageUrl) {
        try {
            minioClient.removeObject(storageProperties.getBucketName(), storageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("minio删除文件失败,storageUrl为:" + storageUrl, e);
            return DefaultResult.failResult(EnumResultCode.E_DELETE_REAL_FILE_FAIL.getInfo());
        }
        return DefaultResult.successResult();
    }
}
