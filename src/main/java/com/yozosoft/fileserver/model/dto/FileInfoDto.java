package com.yozosoft.fileserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-14 19:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDto {

    private Long fileRefId;

    private String fileName;
}
