package com.yozosoft.fileserver.web;

import com.yozosoft.common.exception.ForbiddenAccessException;
import com.yozosoft.fileserver.common.helper.SignHelper;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.dto.DeleteFileDto;
import com.yozosoft.fileserver.dto.ServerUploadFileDto;
import com.yozosoft.fileserver.dto.ServerUploadResultDto;
import com.yozosoft.fileserver.dto.UploadFileDto;
import com.yozosoft.fileserver.model.dto.UploadResultDto;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.sourcefile.ISourceFileManager;
import com.yozosoft.fileserver.utils.JsonResultUtils;
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

    @Autowired
    private SignHelper signHelper;

    @ApiOperation(value = "服务端上传文件")
    @PostMapping("/serverUpload")
    public ResponseEntity uploadByServer(@RequestBody @Valid ServerUploadFileDto serverUploadFileDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
        Boolean checkSignResult = signHelper.checkSign(serverUploadFileDto, nonce, sign);
        if (!checkSignResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        IResult<ServerUploadResultDto> storageResult = iSourceFileManager.storageFileAndSave(serverUploadFileDto);
        if (!storageResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getValue(), null, storageResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult(storageResult.getData()));
    }

    @ApiOperation(value = "服务端上传文件")
    @PostMapping("/serverUploadByFile")
    public ResponseEntity serverUploadByFile(@RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign, @RequestBody MultipartFile multipartFile, ServerUploadFileDto serverUploadFileDto){
        Boolean checkSignResult = signHelper.checkSign(serverUploadFileDto, nonce, sign);
        if (!checkSignResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        IResult<ServerUploadResultDto> storageResult = iSourceFileManager.storageFileAndSave(multipartFile, serverUploadFileDto);
        if (!storageResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getValue(), null, storageResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult(storageResult.getData()));
    }

    @ApiOperation(value = "判断是否可以秒传")
    @GetMapping("/upload")
    public ResponseEntity getFileBySecUpload(@Valid UploadFileDto uploadFileDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
        Boolean checkSignResult = signHelper.checkSign(uploadFileDto, nonce, sign);
        if (!checkSignResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        IResult<YozoFileRefPo> checkResult = iSourceFileManager.checkCanSecUpload(uploadFileDto.getFileMd5(), uploadFileDto.getAppName());
        if (!checkResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_FILE_SEC_UPLOAD_UNABLE.getValue(), null, checkResult.getMessage()));
        }
        YozoFileRefPo yozoFileRefPo = checkResult.getData();
        return sendAppCallBack(yozoFileRefPo, uploadFileDto);
    }

    @ApiOperation(value = "真实上传文件")
    @PostMapping("/upload")
    public ResponseEntity getFileByUpload(@RequestParam(value = "file") MultipartFile multipartFile, @Valid UploadFileDto uploadFileDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
        Boolean checkSignResult = signHelper.checkSign(uploadFileDto, nonce, sign);
        if (!checkSignResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        IResult<YozoFileRefPo> storageResult = iSourceFileManager.storageFileAndSave(multipartFile, uploadFileDto);
        if (!storageResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getValue(), null, storageResult.getMessage()));
        }
        YozoFileRefPo yozoFileRefPo = storageResult.getData();
        return sendAppCallBack(yozoFileRefPo, uploadFileDto);
    }

    @ApiOperation(value = "检查分片文件是否存在")
    @GetMapping("/chunk")
    public ResponseEntity checkChunk(@RequestParam(value = "fileMd5") String fileMd5, @RequestParam(value = "chunk") Integer chunk, @RequestParam(value = "chunkSize") Long chunkSize){
        IResult<String> checkResult = iSourceFileManager.checkChunkFile(fileMd5, chunk, chunkSize);
        if(!checkResult.isSuccess()){
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_CHUNK_FILE_NOT_EXIST.getValue(), null, checkResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult());
    }

    @ApiOperation(value = "上传分片文件")
    @PostMapping("/chunk")
    public ResponseEntity uploadChunk(@RequestParam(value = "file") MultipartFile multipartFile, @RequestParam(value = "fileMd5") String fileMd5, @RequestParam(value = "chunk") Integer chunk){
        IResult<String> storageResult = iSourceFileManager.storageChunkFile(multipartFile, fileMd5, chunk);
        if(!storageResult.isSuccess()){
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_STORAGE_CHUNK_FILE_FAIL.getValue(), null, storageResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult());
    }

    @ApiOperation(value = "合并分片文件并保存")
    @PostMapping("/mergeChunks")
    public ResponseEntity mergeChunks(@Valid UploadFileDto uploadFileDto, @RequestParam(value = "fileName") String fileName, @RequestParam(value = "chunks") Integer chunks, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign){
        Boolean checkSignResult = signHelper.checkSign(uploadFileDto, nonce, sign);
        if (!checkSignResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        IResult<YozoFileRefPo> storageResult = iSourceFileManager.storageFileAndSave(fileName, chunks, uploadFileDto);
        if (!storageResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getValue(), null, storageResult.getMessage()));
        }
        YozoFileRefPo yozoFileRefPo = storageResult.getData();
        return sendAppCallBack(yozoFileRefPo, uploadFileDto);
    }

    @ApiOperation(value = "删除源文件")
    @DeleteMapping("/delete")
    public ResponseEntity deleteFile(@RequestBody @Valid DeleteFileDto deleteFileDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
        Boolean checkSignResult = signHelper.checkSign(deleteFileDto, nonce, sign);
        if (!checkSignResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        IResult<String> deleteResult = iSourceFileManager.deleteFileRef(deleteFileDto);
        if (!deleteResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_DELETE_FILE_FAIL.getValue(), null, deleteResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult());
    }

    private ResponseEntity sendAppCallBack(YozoFileRefPo yozoFileRefPo, UploadFileDto uploadFileDto) {
        IResult<UploadResultDto> sendResult = iSourceFileManager.sendAppCallBack(yozoFileRefPo, uploadFileDto);
        if (!sendResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_UPLOAD_FILE_FAIL.getValue(), null, sendResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult(sendResult.getData()));
    }
}