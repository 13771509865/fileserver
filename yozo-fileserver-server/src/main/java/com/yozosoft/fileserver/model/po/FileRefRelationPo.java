package com.yozosoft.fileserver.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRefRelationPo {
    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer status;

    private Long fileRefId;

    private Integer appId;

    private String remark;
}