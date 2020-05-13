package com.yozosoft.fileserver.common.constants;

import lombok.Getter;

/**
 * @author zhoufeng
 * @description 返回值枚举类
 * @create 2019-04-30 15:03
 **/
@Getter
public enum EnumResultCode {
    /**
     * 返回值枚举类
     */
    E_SUCCESS(0, "操作成功"),
    E_FAIL(1, "操作失败"),
    E_INVALID_PARAM(2, "无效参数"),
    E_SERVER_UNKNOWN_ERROR(3, "服务器未知错误^_^"),
    E_SERVER_BUSY_ERROR(4, "服务器正忙请稍后再试"),

    E_HTTP_SEND_FAIL(10,"http请求失败"),

    E_FILE_SEC_UPLOAD_UNABLE(1000, "文件不存在,不可秒传"),
    E_APP_CALLBACK_URL_ILLEGAL(1001, "app对应回调地址不存在"),
    E_APP_CALLBACK_FAIL(1002, "app回调请求发送失败"),
    E_UPLOAD_FILE_FAIL(1003, "上传文件失败"),
    E_LOCAL_STORAGE_ROOT_ERROR(1004, "本地上传根目录未设置"),
    E_LOCAL_STORAGE_FILE_FAIL(1005, "本地保存上传文件失败"),
    E_DB_STORAGE_FILE_REF_FAIL(1006, "保存上传文件信息失败"),
    E_UPLOAD_FILE_MD5_MISMATCH(1007, "文件md5不匹配");

    private Integer value;
    private String info;

    EnumResultCode(Integer value, String info) {
        this.value = value;
        this.info = info;
    }

    public static EnumResultCode getEnum(Integer value) {
        for (EnumResultCode code : values()) {
            if (code.getValue().equals(value)) {
                return code;
            }
        }
        return null;
    }
}
