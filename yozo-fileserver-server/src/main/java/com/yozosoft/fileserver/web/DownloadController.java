package com.yozosoft.fileserver.web;

import com.yozosoft.common.exception.ForbiddenAccessException;
import com.yozosoft.fileserver.common.helper.SignHelper;
import com.yozosoft.fileserver.common.utils.HttpUtils;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.dto.DownloadResultDto;
import com.yozosoft.fileserver.dto.ServerDownloadDto;
import com.yozosoft.fileserver.dto.UserDownloadDto;
import com.yozosoft.fileserver.model.dto.LocalDownloadDto;
import com.yozosoft.fileserver.service.download.IDownloadManager;
import com.yozosoft.fileserver.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author zhoufeng
 * @description 下载controller
 * @create 2020-05-13 19:35
 **/
@Api(value = "下载Controller", tags = {"下载Controller"})
@Controller
@RequestMapping(value = "/api/file")
@Slf4j
public class DownloadController {

    @Autowired
    private IDownloadManager iDownloadManager;

    @Autowired
    private SignHelper signHelper;

    @ApiOperation(value = "下载文件到服务器制定目录")
    @PostMapping("/serverDownload")
    @ResponseBody
    public ResponseEntity downloadToServer(@RequestBody @Valid ServerDownloadDto serverDownloadDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
        Boolean checkSignResult = signHelper.checkSign(serverDownloadDto, nonce, sign);
        if (!checkSignResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        IResult<List<DownloadResultDto>> downloadResult = iDownloadManager.serverDownload(serverDownloadDto);
        if (!downloadResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getValue(), null, downloadResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult(downloadResult.getData()));
    }

    @ApiOperation(value = "获取下载链接")
    @PostMapping("/downloadUrl")
    @ResponseBody
    public ResponseEntity getDownloadUrl(@RequestBody @Valid UserDownloadDto userDownloadDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
        Boolean checkSignResult = signHelper.checkSign(userDownloadDto, nonce, sign);
        if (!checkSignResult) {
            throw new ForbiddenAccessException(EnumResultCode.E_REQUEST_ILLEGAL.getValue(), EnumResultCode.E_REQUEST_ILLEGAL.getInfo());
        }
        IResult<String> downloadResult = iDownloadManager.getDownloadUrl(userDownloadDto);
        if (!downloadResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_GENERATE_DOWNLOAD_URL_FAIL.getValue(), null, downloadResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult(downloadResult.getData()));
    }

    @ApiOperation(value = "local下载接口")
    @GetMapping("/download/{downloadId}/**")
    public ResponseEntity localDownload(@PathVariable("downloadId") String downloadId) {
        IResult<LocalDownloadDto> localDownloadDtoResult = iDownloadManager.getLocalDownloadDto(downloadId);
        if (!localDownloadDtoResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_DOWNLOAD_FILE_FAIL.getValue(), null, localDownloadDtoResult.getMessage()));
        }
        LocalDownloadDto localDownloadDto = localDownloadDtoResult.getData();
        return ResponseEntity.ok().headers(buildHttpHeaders(localDownloadDto.getFileName()))
                .contentLength(localDownloadDto.getFileSize())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(localDownloadDto.getFileSystemResource());
    }

    private HttpHeaders buildHttpHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        String encodeFileName = HttpUtils.urlEncode(fileName);
        headers.add("Content-Disposition", "attachment;filename=" + encodeFileName + ";filename*=UTF-8''"+encodeFileName);
        return headers;
    }
}
