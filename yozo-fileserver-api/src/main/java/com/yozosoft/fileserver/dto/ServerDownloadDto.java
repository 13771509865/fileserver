package com.yozosoft.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhoufeng
 * @description 服务器下载对象
 * @create 2020-05-14 14:32
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerDownloadDto {

    @NotBlank(message = "来源App未定义")
    private String appName;

    private String storageDir;

    @NotEmpty(message = "需要下载的fileRefId为空")
    private List<FileInfoDto> fileInfos;

}
