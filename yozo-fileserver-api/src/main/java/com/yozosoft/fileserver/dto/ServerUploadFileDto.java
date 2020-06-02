package com.yozosoft.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author zhoufeng
 * @description
 * @create 2020-06-02 15:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerUploadFileDto {

    @NotBlank(message = "来源App未定义")
    private String appName;

    @NotBlank(message = "文件路径未定义")
    private String filePath;

    private String userMetadata;
}
