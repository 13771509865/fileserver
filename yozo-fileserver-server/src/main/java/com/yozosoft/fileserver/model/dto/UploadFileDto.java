package com.yozosoft.fileserver.model.dto;

import com.yozosoft.fileserver.dto.BaseSignDto;
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
public class UploadFileDto extends BaseSignDto {

    @NotBlank(message = "上传文件md5为空")
    private String fileMd5;

    @NotBlank(message = "来源App未定义")
    private String appName;

    private String taskId;

    private String userMetadata;

}
