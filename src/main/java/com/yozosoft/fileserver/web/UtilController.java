package com.yozosoft.fileserver.web;

import com.yozosoft.fileserver.common.utils.JsonResultUtils;
import com.yozosoft.fileserver.common.utils.Md5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-15 16:45
 **/
@Api(value = "文档工具Controller", tags = {"文档工具Controller"})
@RestController
@RequestMapping(value = "/api/util")
public class UtilController {

    @ApiOperation(value = "真实上传文件")
    @PostMapping("/fileMd5")
    public ResponseEntity getFileByUpload(@RequestParam(value = "file") MultipartFile multipartFile) throws IOException {
        String fileMd5 = Md5Utils.getMD5(multipartFile.getInputStream());
        return ResponseEntity.ok(JsonResultUtils.successMapResult(fileMd5));
    }
}
