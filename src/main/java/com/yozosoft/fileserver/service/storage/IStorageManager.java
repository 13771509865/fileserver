package com.yozosoft.fileserver.service.storage;

import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-13 11:24
 **/
public interface IStorageManager {

    /**
     * 生成存储url
     * @param fileName 文件名
     * @return 存储路径
     */
    String generateStorageUrl(String fileName);

    /**
     * 保存fileRef到DB
     * @param multipartFile 文件
     * @param storageUrl 存储路径
     * @param userMetadata 用户自定义数据
     * @param fileMd5 文件md5
     * @return
     */
    IResult<YozoFileRefPo> storageFile(MultipartFile multipartFile, String storageUrl, Map<String, Object> userMetadata, String fileMd5, Integer appId);
}
