package com.yozosoft.fileserver.web;

import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.utils.JsonResultUtils;
import com.yozosoft.fileserver.model.dto.LocalDownloadDto;
import com.yozosoft.fileserver.dto.ServerDownloadDto;
import com.yozosoft.fileserver.dto.UserDownloadDto;
import com.yozosoft.fileserver.service.download.IDownloadManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
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

    @ApiOperation(value = "下载文件到服务器制定目录")
    @GetMapping("/serverDownload")
    @ResponseBody
    public ResponseEntity downloadToServer(@Valid ServerDownloadDto serverDownloadDto) {
        IResult<Map<Long, String>> downloadResult = iDownloadManager.serverDownload(serverDownloadDto);
        if (!downloadResult.isSuccess()) {
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getValue(), null, downloadResult.getMessage()));
        }
        return ResponseEntity.ok(JsonResultUtils.successMapResult(downloadResult.getData()));
    }

    @ApiOperation(value = "获取下载链接")
    @GetMapping("/downloadUrl")
    @ResponseBody
    public ResponseEntity getDownloadUrl(@Valid UserDownloadDto userDownloadDto) {
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
        headers.add("Content-Disposition", "attachment;filename=" + fileName.replaceAll("\\+", "%20"));
        return headers;
    }
}
