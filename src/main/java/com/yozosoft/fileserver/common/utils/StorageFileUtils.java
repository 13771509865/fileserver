package com.yozosoft.fileserver.common.utils;

import com.yozosoft.fileserver.common.constants.StorageConstant;

/**
 * @author zhoufeng
 * @description 存储文件工具类
 * @create 2020-05-25 11:05
 **/
public class StorageFileUtils {

    public static Integer getPartCount(Long fileSize) {
        Long partSize = StorageConstant.PART_SIZE;
        Long partCount = fileSize % partSize == 0 ? fileSize / partSize : fileSize / partSize + 1;
        return partCount.intValue();
    }

    public static Integer getDownloadPartCount(Long fileSize){
        Long partSize = StorageConstant.DOWNLOAD_PART_SIZE;
        Long partCount = fileSize % partSize == 0 ? fileSize / partSize : fileSize / partSize + 1;
        return partCount.intValue();
    }
}
