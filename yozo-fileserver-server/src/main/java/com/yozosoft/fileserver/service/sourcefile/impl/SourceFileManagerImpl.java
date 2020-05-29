package com.yozosoft.fileserver.service.sourcefile.impl;

import com.yozosoft.fileserver.common.constants.EnumAppType;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.FastJsonUtils;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.Md5Utils;
import com.yozosoft.fileserver.dto.DeleteFileDto;
import com.yozosoft.fileserver.dto.UploadFileDto;
import com.yozosoft.fileserver.model.dto.UploadResultDto;
import com.yozosoft.fileserver.model.dto.YozoFileRefDto;
import com.yozosoft.fileserver.model.po.FileRefRelationPo;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.callback.ICallBackService;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import com.yozosoft.fileserver.service.refrelation.IRefRelationService;
import com.yozosoft.fileserver.service.sourcefile.ISourceFileManager;
import com.yozosoft.fileserver.service.sourcefile.ISourceFileService;
import com.yozosoft.fileserver.service.storage.IStorageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private IRefRelationService iRefRelationService;

    @Autowired
    private ISourceFileService iSourceFileService;

    @Override
    public IResult<YozoFileRefPo> checkCanSecUpload(String fileMd5, String appName) {
        IResult<Integer> checkAppResult = EnumAppType.checkAppByName(appName);
        if (!checkAppResult.isSuccess()) {
            return DefaultResult.failResult(checkAppResult.getMessage());
        }
        YozoFileRefPo yozoFileRefPo = iFileRefService.getFileRefByMd5(fileMd5);
        if (yozoFileRefPo == null) {
            return DefaultResult.failResult(EnumResultCode.E_FILE_SEC_UPLOAD_UNABLE.getInfo());
        }
        FileRefRelationPo fileRefRelationPo = iRefRelationService.buildFileRefRelationPo(yozoFileRefPo.getId(), checkAppResult.getData());
        Boolean relationResult = iRefRelationService.insertRefRelationPo(fileRefRelationPo);
        if (!relationResult) {
            return DefaultResult.failResult(EnumResultCode.E_FILE_APP_RELATION_SAVE_FAIL.getInfo());
        }
        return DefaultResult.successResult(yozoFileRefPo);
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
            IResult<Integer> checkAppResult = EnumAppType.checkAppByName(uploadFileDto.getAppName());
            if (!checkAppResult.isSuccess()) {
                return DefaultResult.failResult(checkAppResult.getMessage());
            }
            Integer appId = checkAppResult.getData();
            String fileMd5 = Md5Utils.getMD5(multipartFile.getInputStream());
            String webFileMd5 = uploadFileDto.getFileMd5();
            if (!webFileMd5.equals(fileMd5)) {
                return DefaultResult.failResult(EnumResultCode.E_UPLOAD_FILE_MD5_MISMATCH.getInfo());
            }
            String storageUrl = iStorageManager.generateStorageUrl(multipartFile.getOriginalFilename());
            Map<String, Object> userMetadata = new HashMap<>();
            if (StringUtils.isNotBlank(uploadFileDto.getUserMetadata())) {
                userMetadata = FastJsonUtils.parseJSON2Map(uploadFileDto.getUserMetadata());
            }
            IResult<YozoFileRefPo> storageResult = iStorageManager.storageFile(multipartFile, storageUrl, userMetadata, fileMd5, appId);
            return storageResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件并保存异常", e);
            return DefaultResult.failResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getInfo());
        }
    }

    @Override
    public IResult<String> deleteFileRef(DeleteFileDto deleteFileDto) {
        IResult<Integer> checkAppResult = EnumAppType.checkAppByName(deleteFileDto.getAppName());
        if (!checkAppResult.isSuccess()) {
            return DefaultResult.failResult(checkAppResult.getMessage());
        }
        //验证传过来的fileRefId是不是这个app的
        IResult<List<YozoFileRefPo>> buildResult = iFileRefService.buildStorageUrls(deleteFileDto.getFileRefIds(), checkAppResult.getData());
        if (!buildResult.isSuccess()) {
            return DefaultResult.failResult(buildResult.getMessage());
        }
        List<YozoFileRefPo> yozoFileRefPos = buildResult.getData();
        IResult<String> deleteResult = iSourceFileService.checkAndDeleteFile(yozoFileRefPos, deleteFileDto.getFileRefIds(), checkAppResult.getData());
        return deleteResult;
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
