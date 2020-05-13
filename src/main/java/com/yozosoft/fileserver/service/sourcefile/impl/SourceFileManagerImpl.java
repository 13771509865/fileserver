package com.yozosoft.fileserver.service.sourcefile.impl;

import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.FastJsonUtils;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.Md5Utils;
import com.yozosoft.fileserver.model.dto.UploadFileDto;
import com.yozosoft.fileserver.model.dto.UploadResultDto;
import com.yozosoft.fileserver.model.dto.YozoFileRefDto;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.callback.ICallBackService;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import com.yozosoft.fileserver.service.sourcefile.ISourceFileManager;
import com.yozosoft.fileserver.service.storage.IStorageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author zhoufeng
 * @description 源文档manager实现类
 * @create 2020-05-12 15:48
 **/
@Service("sourceFileManagerImpl")
@Slf4j
public class SourceFileManagerImpl implements ISourceFileManager {

    @Autowired
    private IFileRefService iFileRefService;

    @Autowired
    private ICallBackService iCallBackService;

    @Autowired
    private IStorageManager iStorageManager;

    @Override
    public IResult<YozoFileRefPo> checkCanSecUpload(String fileMd5) {
        YozoFileRefPo yozoFileRefPo = iFileRefService.getFileRefByMd5(fileMd5);
        return yozoFileRefPo != null ? DefaultResult.successResult(yozoFileRefPo) : DefaultResult.failResult(EnumResultCode.E_FILE_SEC_UPLOAD_UNABLE.getInfo());
    }

    @Override
    public IResult<UploadResultDto> sendAppCallBack(YozoFileRefPo yozoFileRefPo, UploadFileDto uploadFileDto) {
        YozoFileRefDto yozoFileRefDto = buildYozoFileRefDto(yozoFileRefPo, uploadFileDto);
        IResult<Map<String, Object>> sendResult = iCallBackService.sendCallBackUrlByApp(uploadFileDto.getAppName(), yozoFileRefDto);
        if (!sendResult.isSuccess()) {
            return DefaultResult.failResult(sendResult.getMessage());
        }
        return DefaultResult.successResult(buildUploadResultDto(yozoFileRefDto, sendResult.getData()));
    }

    @Override
    public IResult<YozoFileRefPo> storageFileAndSave(MultipartFile multipartFile, UploadFileDto uploadFileDto) {
        try {
            String fileMd5 = Md5Utils.getMD5(multipartFile.getInputStream());
            String webFileMd5 = uploadFileDto.getFileMd5();
            if (!webFileMd5.equals(fileMd5)) {
                return DefaultResult.failResult(EnumResultCode.E_UPLOAD_FILE_MD5_MISMATCH.getInfo());
            }
            String storageUrl = iStorageManager.generateStorageUrl(multipartFile.getOriginalFilename());
            IResult<YozoFileRefPo> storageResult = iStorageManager.storageFile(multipartFile, storageUrl, FastJsonUtils.parseJSON2Map(uploadFileDto.getUserMetadata()), fileMd5);
            return storageResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件并保存异常", e);
            return DefaultResult.failResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getInfo());
        }
    }

    private YozoFileRefDto buildYozoFileRefDto(YozoFileRefPo yozoFileRefPo, UploadFileDto uploadFileDto) {
        YozoFileRefDto yozoFileRefDto = new YozoFileRefDto(yozoFileRefPo.getId(), yozoFileRefPo.getFileSize(), uploadFileDto.getTaskId(), uploadFileDto.getUserMetadata());
        return yozoFileRefDto;
    }

    private UploadResultDto buildUploadResultDto(YozoFileRefDto yozoFileRefDto, Map<String, Object> appResponseData) {
        UploadResultDto uploadResultDto = new UploadResultDto();
        uploadResultDto.setId(yozoFileRefDto.getId());
        uploadResultDto.setFileSize(yozoFileRefDto.getFileSize());
        uploadResultDto.setAppResponseData(appResponseData);
        return uploadResultDto;
    }
}
