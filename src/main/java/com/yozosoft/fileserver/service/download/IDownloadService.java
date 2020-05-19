package com.yozosoft.fileserver.service.download;

import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.dto.FileInfoDto;
import com.yozosoft.fileserver.model.dto.FileRefInfoDto;
import com.yozosoft.fileserver.model.dto.ServerDownloadDto;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-14 17:21
 **/
public interface IDownloadService {

    /**
     * 获取文件名
     * @param fileName 用户传过来的文件名
     * @param storageUrl 存储url
     * @return 文件名
     */
    String getTargetFileName(String fileName, String storageUrl);

    String buildStorageDir(ServerDownloadDto serverDownloadDto);

    IResult<List<YozoFileRefPo>> buildStorageUrls(List<Long> fileRefIds, Integer appId);

    List<FileRefInfoDto> buildFileRefInfos(List<YozoFileRefPo> fileRefs, List<FileInfoDto> fileInfos);

    File buildZipDir();

    File buildZipFile(File zipDir, String fileName);

    Long buildTimeOut(Long timeOut);
}
