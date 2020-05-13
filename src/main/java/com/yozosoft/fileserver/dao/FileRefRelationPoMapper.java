package com.yozosoft.fileserver.dao;

import com.yozosoft.fileserver.model.po.FileRefRelationPo;

public interface FileRefRelationPoMapper {

    int insertSelective(FileRefRelationPo record);
}