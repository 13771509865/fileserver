package com.yozosoft.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author zhoufeng
 * @description 上传文件对象
 * @create 2020-05-12 18:50
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileDto{

    @NotBlank(message = "上传文件md5为空")
    private String fileMd5;

    @NotBlank(message = "来源App未定义")
    private String appName;

    private String taskId;

    private String userMetadata;

}
