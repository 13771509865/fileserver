package com.yozosoft.fileserver.dao;

import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhoufeng
 * @description fileRef Dao层
 * @create 2020-05-12 15:27
 **/
public interface YozoFileRefPoMapper {

    int insertSelective(YozoFileRefPo record);

    YozoFileRefPo selectByFileMd5(String fileMd5);

    List<YozoFileRefPo> selectByCheckApp(@Param("fileRefIds") List<Long> fileRefIds, @Param("appId") Integer appId);

    int deleteByIds(@Param("fileRefIds") List<Long> fileRefId);
}