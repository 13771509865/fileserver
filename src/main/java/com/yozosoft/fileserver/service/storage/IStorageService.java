package com.yozosoft.fileserver.service.storage;

import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.dto.FileRefInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author zhoufeng
 * @description 存储service
 * @create 2020-05-13 10:55
 **/
public interface IStorageService {

    /**
     * 存储文件
     *
     * @param multipartFile 文件
     * @param storageUrl    存储路径
     * @param userMetadata  用户自定义数据
     * @return 存储结果
     */
    IResult<String> storageFile(MultipartFile multipartFile, String storageUrl, Map<String, Object> userMetadata);

    IResult<Map<Long, String>> downloadFileToServer(List<FileRefInfoDto> storageUrls, String storageDir);

    IResult<String> generateDownloadUrl(List<FileRefInfoDto> storageUrls, String fileName, Long timeOut);
}
