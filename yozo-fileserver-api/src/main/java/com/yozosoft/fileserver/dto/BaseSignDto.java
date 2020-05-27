package com.yozosoft.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-27 08:39
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseSignDto {

    protected String nonce;

    protected String sign;
}
