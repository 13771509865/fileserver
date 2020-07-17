package com.yozosoft.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhoufeng
 * @description
 * @create 2020-07-17 09:27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResultDto {

    private Long fileRefId;

    private String downloadPath;

}
