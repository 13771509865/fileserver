package com.yozosoft.fileserver.service.fileref;

import com.yozosoft.fileserver.model.po.YozoFileRefPo;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-12 15:16
 **/
public interface IFileRefService {

    /**
     * 根据文件md5查询是否存在
     *
     * @param fileMd5 文件md5
     * @return 是否存在
     */
    YozoFileRefPo getFileRefByMd5(String fileMd5);

    /**
     * 插入fileRef记录
     * @param yozoFileRefPo fileRef对象
     * @return 插入结果
     */
    Boolean insertFileRefPo(YozoFileRefPo yozoFileRefPo);

    YozoFileRefPo buildYozoFileRefPo(String fileMd5, String storageUrl, Long fileSize);
}
