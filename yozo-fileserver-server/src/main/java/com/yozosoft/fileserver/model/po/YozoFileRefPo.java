package com.yozosoft.fileserver.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YozoFileRefPo {
    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer status;

    private String fileMd5;

    private String storageUrl;

    private Long fileSize;

    private String remark;

    /**
     * 业务上用的
     */
    private Boolean isExist;
}