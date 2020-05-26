package com.yozosoft.fileserver.service.sourcefile;

import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.dto.DeleteFileDto;
import com.yozosoft.fileserver.model.dto.UploadFileDto;
import com.yozosoft.fileserver.model.dto.UploadResultDto;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhoufeng
 * @description 源文档manager
 * @create 2020-05-12 15:47
 **/
public interface ISourceFileManager {

    /**
     * 检查是否可以秒传
     *
     * @param fileMd5 文件md5
     * @return result
     */
    IResult<YozoFileRefPo> checkCanSecUpload(String fileMd5, String appName);

    IResult<UploadResultDto> sendAppCallBack(YozoFileRefPo yozoFileRefPo, UploadFileDto uploadFileDto);

    IResult<YozoFileRefPo> storageFileAndSave(MultipartFile multipartFile, UploadFileDto uploadFileDto);

    IResult<String> deleteFileRef(DeleteFileDto deleteFileDto);
}
