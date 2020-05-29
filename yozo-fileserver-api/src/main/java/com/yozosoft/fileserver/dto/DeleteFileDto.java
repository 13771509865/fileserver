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
 * @create 2020-05-22 14:07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteFileDto{

    @NotEmpty(message = "需要删除的fileRefId为空")
    private List<Long> fileRefIds;

    @NotBlank(message = "来源App未定义")
    private String appName;
}
