package com.yozosoft.fileserver.service.download;

import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.dto.DownloadResultDto;
import com.yozosoft.fileserver.model.dto.LocalDownloadDto;
import com.yozosoft.fileserver.dto.ServerDownloadDto;
import com.yozosoft.fileserver.dto.UserDownloadDto;

import java.util.List;
import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-14 16:58
 **/
public interface IDownloadManager {

    IResult<List<DownloadResultDto>> serverDownload(ServerDownloadDto serverDownloadDto);

    IResult<String> getDownloadUrl(UserDownloadDto userDownloadDto);

    IResult<LocalDownloadDto> getLocalDownloadDto(String downloadId);
}
