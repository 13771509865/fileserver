package com.yozosoft.fileserver.service.refrelation;

import com.yozosoft.fileserver.model.po.FileRefRelationPo;

/**
 * @author zhoufeng
 * @description ref关系service
 * @create 2020-05-14 09:30
 **/
public interface IRefRelationService {

    /**
     * 插入ref关系数据
     *
     * @param fileRefRelationPo 插入对象
     * @return 结果
     */
    Boolean insertRefRelationPo(FileRefRelationPo fileRefRelationPo);

    /**
     * 构建ref app关系对象
     *
     * @param fileRefId 文件refID
     * @param appId     appId
     * @return 对象
     */
    FileRefRelationPo buildFileRefRelationPo(Long fileRefId, Integer appId);

    FileRefRelationPo selectByRefAndApp(Long fileRefId, Integer appId);
}
