package com.yozosoft.fileserver.web;

import com.yozosoft.fileserver.common.constants.EnumResultCode;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.JsonResultUtils;
import com.yozosoft.fileserver.model.dto.UploadFileDto;
import com.yozosoft.fileserver.model.dto.UploadResultDto;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.sourcefile.ISourceFileManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @author zhoufeng
 * @description 文件controller
 * @create 2020-05-12 14:46
 **/
@Api(value = "文档Controller", tags = {"文档Controller"})
@RestController
@RequestMapping(value = "/api/file")
@Slf4j
public class SourceFileController {

    @Autowired
    private ISourceFileManager iSourceFileManager;

    @ApiOperation(value = "判断是否可以秒传")
    @GetMapping("/upload")
    public ResponseEntity getFileBySecUpload(@Valid UploadFileDto uploadFileDto) {
        IResult<YozoFileRefPo> checkResult = iSourceFileManager.checkCanSecUpload(uploadFileDto.getFileMd5(), uploadFileDto.getAppName());
        if (!checkResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_FILE_SEC_UPLOAD_UNABLE.getValue(), null, checkResult.getMessage()));
        }
        YozoFileRefPo yozoFileRefPo = checkResult.getData();
        return sendAppCallBack(yozoFileRefPo, uploadFileDto);
    }

    @ApiOperation(value = "真实上传文件")
    @PostMapping("/upload")
    public ResponseEntity getFileByUpload(@RequestParam(value = "file") MultipartFile multipartFile, @Valid UploadFileDto uploadFileDto) {
        IResult<YozoFileRefPo> storageResult = iSourceFileManager.storageFileAndSave(multipartFile, uploadFileDto);
        if (!storageResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getValue(), null, storageResult.getMessage()));
        }
        YozoFileRefPo yozoFileRefPo = storageResult.getData();
        return sendAppCallBack(yozoFileRefPo, uploadFileDto);
    }

    private ResponseEntity sendAppCallBack(YozoFileRefPo yozoFileRefPo, UploadFileDto uploadFileDto) {
        IResult<UploadResultDto> sendResult = iSourceFileManager.sendAppCallBack(yozoFileRefPo, uploadFileDto);
        if (!sendResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getValue(), null, sendResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult(sendResult.getData()));
    }
}