package com.yozosoft.fileserver.service.refrelation;

import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.model.po.FileRefRelationPo;

import java.util.List;

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
    IResult<Boolean> insertRefRelationPo(FileRefRelationPo fileRefRelationPo);

    /**
     * 构建ref app关系对象
     *
     * @param fileRefId 文件refID
     * @param appId     appId
     * @return 对象
     */
    FileRefRelationPo buildFileRefRelationPo(Long fileRefId, Integer appId);

    List<FileRefRelationPo> selectByQuery(Long fileRefId, Integer appId);

    Boolean deleteRefRelation(List<Long> fileRefIds, Integer appId);

    List<Long> selectUsedFileRefIds(List<Long> fileRefIds);
}
