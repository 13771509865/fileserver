package com.yozosoft.fileserver.dao;

import com.yozosoft.fileserver.model.po.FileRefRelationPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileRefRelationPoMapper {

    int insertSelective(FileRefRelationPo record);

    List<FileRefRelationPo> selectByQuery(@Param("fileRefId") Long fileRefId, @Param("appId") Integer appId);

    int deleteByRefIdAndAppId(@Param("fileRefIds") List<Long> fileRefId, @Param("appId") Integer appId);

    List<Long> selectUsedFileRefIds(@Param("fileRefIds") List<Long> fileRefIds);
}