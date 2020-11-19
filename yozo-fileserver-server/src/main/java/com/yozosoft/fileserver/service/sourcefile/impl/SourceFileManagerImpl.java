package com.yozosoft.fileserver.service.sourcefile.impl;

import com.yozosoft.fileserver.common.utils.AppUtils;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.Md5Utils;
import com.yozosoft.fileserver.constants.EnumAppType;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.dto.DeleteFileDto;
import com.yozosoft.fileserver.dto.ServerUploadFileDto;
import com.yozosoft.fileserver.dto.ServerUploadResultDto;
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
import com.yozosoft.fileserver.utils.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
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
        IResult<Integer> checkAppResult = AppUtils.checkAppByName(appName);
        if (!checkAppResult.isSuccess()) {
            return DefaultResult.failResult(checkAppResult.getMessage());
        }
        YozoFileRefPo yozoFileRefPo = iFileRefService.getFileRefByMd5(fileMd5);
        if (yozoFileRefPo == null) {
            return DefaultResult.failResult(EnumResultCode.E_FILE_SEC_UPLOAD_UNABLE.getInfo());
        }
        FileRefRelationPo fileRefRelationPo = iRefRelationService.buildFileRefRelationPo(yozoFileRefPo.getId(), checkAppResult.getData());
        IResult<Boolean> relationResult = iRefRelationService.insertRefRelationPo(fileRefRelationPo);
        if (!relationResult.isSuccess()) {
            return DefaultResult.failResult(EnumResultCode.E_FILE_APP_RELATION_SAVE_FAIL.getInfo());
        }
        yozoFileRefPo.setIsExist(relationResult.getData());
        return DefaultResult.successResult(yozoFileRefPo);
    }

    @Override
    public IResult<UploadResultDto> sendAppCallBack(YozoFileRefPo yozoFileRefPo, UploadFileDto uploadFileDto) {
        Boolean isExist = yozoFileRefPo.getIsExist() == null ? false : yozoFileRefPo.getIsExist();
        List<Long> fileRefIds = Arrays.asList(yozoFileRefPo.getId());
        Integer appId = EnumAppType.getEnum(uploadFileDto.getAppName()).getAppId();
        try {
            YozoFileRefDto yozoFileRefDto = buildYozoFileRefDto(yozoFileRefPo, uploadFileDto);
            IResult<Map<String, Object>> sendResult = iCallBackService.sendCallBackUrlByApp(uploadFileDto.getAppName(), yozoFileRefDto);
            if (!sendResult.isSuccess()) {
                if (!isExist) {
                    //删除关联关系
                    iRefRelationService.deleteRefRelation(fileRefIds, appId);
                }
                return DefaultResult.failResult(sendResult.getMessage());
            }
            return DefaultResult.successResult(buildUploadResultDto(yozoFileRefDto, sendResult.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            if (!isExist) {
                //删除关联关系
                iRefRelationService.deleteRefRelation(fileRefIds, appId);
            }
            return DefaultResult.failResult(EnumResultCode.E_APP_CALLBACK_FAIL.getInfo());
        }
    }

    @Override
    public IResult<YozoFileRefPo> storageFileAndSave(MultipartFile multipartFile, UploadFileDto uploadFileDto) {
        try {
            IResult<Integer> checkAppResult = AppUtils.checkAppByName(uploadFileDto.getAppName());
            if (!checkAppResult.isSuccess()) {
                return DefaultResult.failResult(checkAppResult.getMessage());
            }
            if (!checkEmptyFile(null, multipartFile)) {
                return DefaultResult.failResult(EnumResultCode.E_FILE_SIZE_ILLEGAL.getInfo());
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
    public IResult<YozoFileRefPo> storageFileAndSave(String fileName, Integer chunks, UploadFileDto uploadFileDto) {
        try{
            String fileMd5 = uploadFileDto.getFileMd5();
            IResult<Integer> checkAppResult = AppUtils.checkAppByName(uploadFileDto.getAppName());
            if (!checkAppResult.isSuccess()) {
                return DefaultResult.failResult(checkAppResult.getMessage());
            }
            IResult<File> mergeResult = iSourceFileService.mergeChunkFile(fileMd5, chunks, fileName);
            if(!mergeResult.isSuccess()){
                return DefaultResult.failResult(mergeResult.getMessage());
            }
            File storageFile = mergeResult.getData();
            if (!checkEmptyFile(storageFile, null)) {
                return DefaultResult.failResult(EnumResultCode.E_FILE_SIZE_ILLEGAL.getInfo());
            }
            Integer appId = checkAppResult.getData();
            String storageUrl = iStorageManager.generateStorageUrl(fileName);
            Map<String, Object> userMetadata = new HashMap<>();
            if (StringUtils.isNotBlank(uploadFileDto.getUserMetadata())) {
                userMetadata = FastJsonUtils.parseJSON2Map(uploadFileDto.getUserMetadata());
            }
            IResult<YozoFileRefPo> result = iStorageManager.storageFile(storageFile, storageUrl, userMetadata, fileMd5, appId);
            //删除合并后的临时文件
            storageFile.delete();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            log.error("上传文件并保存异常", e);
            return DefaultResult.failResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getInfo());
        }
    }

    @Override
    public IResult<ServerUploadResultDto> storageFileAndSave(ServerUploadFileDto serverUploadFileDto) {
        try {
            IResult<Integer> checkAppResult = AppUtils.checkAppByName(serverUploadFileDto.getAppName());
            if (!checkAppResult.isSuccess()) {
                return DefaultResult.failResult(checkAppResult.getMessage());
            }
            Integer appId = checkAppResult.getData();
            File storageFile = new File(serverUploadFileDto.getFilePath());
            if (!storageFile.isFile()) {
                return DefaultResult.failResult(EnumResultCode.E_SERVER_UPLOAD_PATH_NOT_EXIST.getInfo());
            }
            if (!checkEmptyFile(storageFile, null)) {
                return DefaultResult.failResult(EnumResultCode.E_FILE_SIZE_ILLEGAL.getInfo());
            }
            String fileMd5 = Md5Utils.getMD5(storageFile);
            YozoFileRefPo yozoFileRefPo = iFileRefService.getFileRefByMd5(fileMd5);
            if (yozoFileRefPo != null) {
                //插入relation表
                return insertRefRelation(yozoFileRefPo, appId);
            }
            //上传文件
            String storageUrl = iStorageManager.generateStorageUrl(storageFile.getName());
            Map<String, Object> userMetadata = builduserMetadata(serverUploadFileDto.getUserMetadata());
            IResult<YozoFileRefPo> storageResult = iStorageManager.storageFile(storageFile, storageUrl, userMetadata, fileMd5, appId);
            if (!storageResult.isSuccess()) {
                return DefaultResult.failResult(storageResult.getMessage());
            }
            YozoFileRefPo yozoFileRefPoResult = storageResult.getData();
            return DefaultResult.successResult(new ServerUploadResultDto(yozoFileRefPoResult.getId(), false, yozoFileRefPoResult.getStorageUrl(), yozoFileRefPoResult.getFileMd5(), yozoFileRefPoResult.getFileSize()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件并保存异常", e);
            return DefaultResult.failResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getInfo());
        }
    }

    @Override
    public IResult<ServerUploadResultDto> storageFileAndSave(MultipartFile multipartFile, ServerUploadFileDto serverUploadFileDto) {
        try {
            IResult<Integer> checkAppResult = AppUtils.checkAppByName(serverUploadFileDto.getAppName());
            if (!checkAppResult.isSuccess()) {
                return DefaultResult.failResult(checkAppResult.getMessage());
            }
            if (!checkEmptyFile(null, multipartFile)) {
                return DefaultResult.failResult(EnumResultCode.E_FILE_SIZE_ILLEGAL.getInfo());
            }
            Integer appId = checkAppResult.getData();
            String fileMd5 = Md5Utils.getMD5(multipartFile.getInputStream());
            YozoFileRefPo yozoFileRefPo = iFileRefService.getFileRefByMd5(fileMd5);
            if (yozoFileRefPo != null) {
                return insertRefRelation(yozoFileRefPo, appId);
            }
            //上传文件
            String storageUrl = iStorageManager.generateStorageUrl(multipartFile.getOriginalFilename());
            Map<String, Object> userMetadata = builduserMetadata(serverUploadFileDto.getUserMetadata());
            IResult<YozoFileRefPo> storageResult = iStorageManager.storageFile(multipartFile, storageUrl, userMetadata, fileMd5, appId);
            if (!storageResult.isSuccess()) {
                return DefaultResult.failResult(storageResult.getMessage());
            }
            YozoFileRefPo yozoFileRefPoResult = storageResult.getData();
            return DefaultResult.successResult(new ServerUploadResultDto(yozoFileRefPoResult.getId(), false, yozoFileRefPoResult.getStorageUrl(), yozoFileRefPoResult.getFileMd5(), yozoFileRefPoResult.getFileSize()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件并保存异常", e);
            return DefaultResult.failResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getInfo());
        }
    }

    @Override
    public IResult<String> deleteFileRef(DeleteFileDto deleteFileDto) {
        IResult<Integer> checkAppResult = AppUtils.checkAppByName(deleteFileDto.getAppName());
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

    @Override
    public IResult<String> storageChunkFile(MultipartFile multipartFile, String fileMd5, Integer chunk) {
        String chunkFolderPath = iSourceFileService.getChunkFolderPath(fileMd5);
        File parentFile = new File(chunkFolderPath);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        IResult<String> storageResult = iSourceFileService.storageChunkFile(chunkFolderPath, chunk, multipartFile);
        return storageResult;
    }

    @Override
    public IResult<String> checkChunkFile(String fileMd5, Integer chunk, Long chunkSize) {
        String chunkFolderPath = iSourceFileService.getChunkFolderPath(fileMd5);
        File chunkFile = new File(chunkFolderPath, chunk + "");
        if (chunkFile.isFile()) {
            if(chunkFile.length() == chunkSize){
                return DefaultResult.successResult();
            }
            //如果当前分片文件存在但是大小不一致,比如前端改了分块大小
            chunkFile.delete();
        }
        return DefaultResult.failResult(EnumResultCode.E_CHUNK_FILE_NOT_EXIST.getInfo());
    }

    private Map<String, Object> builduserMetadata(String userMetadata) {
        Map<String, Object> userMetadataMap = new HashMap<>();
        if (StringUtils.isNotBlank(userMetadata)) {
            userMetadataMap = FastJsonUtils.parseJSON2Map(userMetadata);
        }
        return userMetadataMap;
    }

    private IResult<ServerUploadResultDto> insertRefRelation(YozoFileRefPo yozoFileRefPo, Integer appId) {
        //插入relation表
        FileRefRelationPo fileRefRelationPo = iRefRelationService.buildFileRefRelationPo(yozoFileRefPo.getId(), appId);
        IResult<Boolean> relationResult = iRefRelationService.insertRefRelationPo(fileRefRelationPo);
        if (!relationResult.isSuccess()) {
            return DefaultResult.failResult(EnumResultCode.E_FILE_APP_RELATION_SAVE_FAIL.getInfo());
        }
        return DefaultResult.successResult(new ServerUploadResultDto(yozoFileRefPo.getId(), relationResult.getData(), yozoFileRefPo.getStorageUrl(), yozoFileRefPo.getFileMd5(), yozoFileRefPo.getFileSize()));
    }

    private YozoFileRefDto buildYozoFileRefDto(YozoFileRefPo yozoFileRefPo, UploadFileDto uploadFileDto) {
        YozoFileRefDto yozoFileRefDto = new YozoFileRefDto();
        yozoFileRefDto.setFileRefId(yozoFileRefPo.getId());
        yozoFileRefDto.setFileSize(yozoFileRefPo.getFileSize());
        yozoFileRefDto.setTaskId(uploadFileDto.getTaskId());
        yozoFileRefDto.setStorageUrl(yozoFileRefPo.getStorageUrl());
        yozoFileRefDto.setFileMd5(yozoFileRefPo.getFileMd5());
        yozoFileRefDto.setUserMetadata(uploadFileDto.getUserMetadata());
        return yozoFileRefDto;
    }

    private UploadResultDto buildUploadResultDto(YozoFileRefDto yozoFileRefDto, Map<String, Object> appResponseData) {
        UploadResultDto uploadResultDto = new UploadResultDto();
        uploadResultDto.setId(yozoFileRefDto.getFileRefId());
        uploadResultDto.setFileSize(yozoFileRefDto.getFileSize());
        uploadResultDto.setAppResponseData(appResponseData);
        return uploadResultDto;
    }

    private Boolean checkEmptyFile(File file, MultipartFile multipartFile) {
        if (file != null) {
            return file.length() > 0;
        } else if (multipartFile != null) {
            return multipartFile.getSize() > 0;
        }
        return false;
    }
}
