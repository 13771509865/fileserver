package com.yozosoft.fileserver.service.storage;

import com.yozosoft.fileserver.common.utils.IResult;

import java.io.File;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-21 11:36
 **/
public interface IStorageClient {

    IResult<String> uploadFile(File file, String storageUrl, Map<String, Object> userMetadata);

    IResult<String> downloadFile(String storageUrl, File targetFile);

    IResult<String> generateUrl(String storageUrl, String fileName, Long timeOut);

    IResult<String> deleteFile(String storageUrl);
}
