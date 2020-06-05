package com.yozosoft.fileserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-12 19:48
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YozoFileRefDto {

    private Long fileRefId;

    private Long fileSize;

    private String taskId;

    private String userMetadata;
}
