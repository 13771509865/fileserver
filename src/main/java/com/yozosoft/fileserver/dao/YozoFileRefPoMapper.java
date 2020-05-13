package com.yozosoft.fileserver.dao;

import com.yozosoft.fileserver.model.po.YozoFileRefPo;

/**
 * @author zhoufeng
 * @description fileRef Dao层
 * @create 2020-05-12 15:27
 **/
public interface YozoFileRefPoMapper {

    int insertSelective(YozoFileRefPo record);

    YozoFileRefPo selectByFileMd5(String fileMd5);
}