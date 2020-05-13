package com.yozosoft.fileserver.web;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhoufeng
 * @description 下载controller
 * @create 2020-05-13 19:35
 **/
@Api(value = "下载Controller", tags = {"下载Controller"})
@RestController
@RequestMapping(value = "/api/file")
@Slf4j
public class DownloadController {

}
