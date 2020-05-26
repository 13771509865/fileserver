package com.yozosoft.fileserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-13 10:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDto {

    private Long id;

    private Long fileSize;

    private Map<String,Object> appResponseData;
}
