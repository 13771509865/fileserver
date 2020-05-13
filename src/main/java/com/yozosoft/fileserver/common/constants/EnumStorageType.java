package com.yozosoft.fileserver.common.constants;

import lombok.Getter;

/**
 * @author zhoufeng
 * @description 存储枚举类
 * @create 2020-05-13 10:59
 **/
@Getter
public enum EnumStorageType {

    /**
     * 存储枚举
     */
    E_LOCAL_STORAGE(0, "local", "本地存储"),
    E_ALI_OSS_STORAGE(1, "aliOss", "阿里Oss对象存储"),
    E_HW_OBS_STORAGE(2, "hwObs", "华为Obs对象存储");

    private Integer typeId;
    private String typeName;
    private String typeDesc;

    EnumStorageType(Integer typeId, String typeName, String typeDesc){
        this.typeId = typeId;
        this.typeName = typeName;
        this.typeDesc = typeDesc;
    }
}
