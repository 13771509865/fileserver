package com.yozosoft.fileserver.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhoufeng
 * @description http返回结果
 * @create 2020-05-12 19:41
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResultEntity {
    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应体
     */
    private String body;
}
