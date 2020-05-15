package com.yozosoft.fileserver.dao;

import com.yozosoft.fileserver.model.po.FileRefRelationPo;
import org.apache.ibatis.annotations.Param;

public interface FileRefRelationPoMapper {

    int insertSelective(FileRefRelationPo record);

    FileRefRelationPo selectByRefIdAndAppId(@Param("fileRefId")Long fileRefId, @Param("appId")Integer appId);
}