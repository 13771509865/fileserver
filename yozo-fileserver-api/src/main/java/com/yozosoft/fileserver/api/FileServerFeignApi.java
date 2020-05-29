package com.yozosoft.fileserver.api;

import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.dto.DeleteFileDto;
import com.yozosoft.fileserver.dto.ServerDownloadDto;
import com.yozosoft.fileserver.dto.UserDownloadDto;
import com.yozosoft.fileserver.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author zhoufeng
 * @description fileserver feign api
 * @create 2020-05-26 17:11
 **/
@ConditionalOnProperty(value = "fileserver.innerFeign.enable", havingValue = "true", matchIfMissing = true)
@FeignClient(name = "fileserver", fallback = FileServerFeignApi.FileServerFeignApiFallBack.class)
public interface FileServerFeignApi {

    @PostMapping("/api/file/serverDownload")
    ResponseEntity<Map<String, Object>> downloadToServer(@Valid ServerDownloadDto serverDownloadDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign);

    @PostMapping("/api/file/downloadUrl")
    ResponseEntity<Map<String, Object>> getDownloadUrl(@Valid UserDownloadDto userDownloadDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign);

    @DeleteMapping("/api/file/delete")
    ResponseEntity deleteFile(@Valid DeleteFileDto deleteFileDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign);

    @Slf4j
    @Component
    class FileServerFeignApiFallBack implements FileServerFeignApi {

        @Override
        public ResponseEntity<Map<String, Object>> downloadToServer(@Valid ServerDownloadDto serverDownloadDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
            log.error("下载文件到服务器失败");
//            return new ResponseEntity<>(JsonResultUtils.buildMapResultByResultCode(EnumResultCode.E_SERVER_DOWNLOAD_FAIL), HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_SERVER_DOWNLOAD_FAIL.getValue(), null, "下载文件到服务器熔断失败"));
        }

        @Override
        public ResponseEntity<Map<String, Object>> getDownloadUrl(@Valid UserDownloadDto userDownloadDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
            log.error("获取文件下载链接失败");
//            return new ResponseEntity<>(JsonResultUtils.buildMapResultByResultCode(EnumResultCode.E_GENERATE_DOWNLOAD_URL_FAIL), HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_GENERATE_DOWNLOAD_URL_FAIL.getValue(), null, "生成文档下载Url熔断失败"));
        }

        @Override
        public ResponseEntity deleteFile(@Valid DeleteFileDto deleteFileDto, @RequestParam(value = "nonce") String nonce, @RequestParam(value = "sign") String sign) {
            log.error("删除文件失败");
//            return new ResponseEntity<>(JsonResultUtils.buildMapResultByResultCode(EnumResultCode.E_DELETE_FILE_FAIL), HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_DELETE_FILE_FAIL.getValue(), null, "删除文件熔断失败"));
        }
    }
}
