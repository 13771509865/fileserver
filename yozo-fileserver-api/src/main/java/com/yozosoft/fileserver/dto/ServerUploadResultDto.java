package com.yozosoft.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhoufeng
 * @description
 * @create 2020-06-02 15:56
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerUploadResultDto {

    private Long fileRefId;

    private Boolean isExist;

    private String storageUrl;

    private String fileMd5;

    private Long fileSize;
}
