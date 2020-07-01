package com.yozosoft.fileserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-14 19:36
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRefInfoDto {

    private Long fileRefId;

    private String storageUrl;

    private String fileName;

    private String fileRelativePath;
}
