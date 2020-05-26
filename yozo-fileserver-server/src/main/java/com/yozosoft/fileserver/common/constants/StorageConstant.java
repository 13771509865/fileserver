package com.yozosoft.fileserver.common.constants;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-13 11:09
 **/
public class StorageConstant {

    public static final String LOCAL_STORAGE_TYPE_ID = "0";

    public static final String ALI_OSS_STORAGE_TYPE_ID = "1";

    public static final String HW_OBS_STORAGE_TYPE_ID = "2";

    public static final String STORAGE_SEPARATOR = "/";

    public static final String DOCUMENT_PATH = "document";

    public static final String ZIP_PATH = "zip";

    public static final String UNIFIED_FILENAME = "babel";

    public static final String MULTIPLE_DOWNLOAD_FILENAME = "yozo";

    public static final Long PART_SIZE = 20 * 1024 * 1024L;

    public static final Long DOWNLOAD_PART_SIZE = 10 * 1024 * 1024L;
}
