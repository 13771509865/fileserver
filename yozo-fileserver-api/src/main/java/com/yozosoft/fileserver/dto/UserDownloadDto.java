package com.yozosoft.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-14 18:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDownloadDto {

    @NotBlank(message = "来源App未定义")
    private String appName;

    @NotEmpty(message = "需要下载的fileRefId为空")
    private List<FileInfoDto> fileInfos;

    private String fileName;

    private Long timeOut;
}
