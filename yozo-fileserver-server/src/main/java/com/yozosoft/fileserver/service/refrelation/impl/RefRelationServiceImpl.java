package com.yozosoft.fileserver.service.refrelation.impl;

import com.yozosoft.fileserver.common.utils.DateViewUtils;
import com.yozosoft.fileserver.dao.FileRefRelationPoMapper;
import com.yozosoft.fileserver.model.po.FileRefRelationPo;
import com.yozosoft.fileserver.service.refrelation.IRefRelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-14 11:46
 **/
@Service("refRelationServiceImpl")
@Slf4j
public class RefRelationServiceImpl implements IRefRelationService {

    @Autowired
    private FileRefRelationPoMapper fileRefRelationPoMapper;

    @Autowired
    private SnowflakeShardingKeyGenerator snowflakeShardingKeyGenerator;

    @Override
    public Boolean insertRefRelationPo(FileRefRelationPo fileRefRelationPo) {
        try {
            int insertResult = fileRefRelationPoMapper.insertSelective(fileRefRelationPo);
            return insertResult > 0;
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            return true;
        }
    }

    @Override
    public List<FileRefRelationPo> selectByQuery(Long fileRefId, Integer appId) {
        List<FileRefRelationPo> fileRefRelationPos = fileRefRelationPoMapper.selectByQuery(fileRefId, appId);
        return fileRefRelationPos;
    }

    @Override
    public FileRefRelationPo buildFileRefRelationPo(Long fileRefId, Integer appId) {
        FileRefRelationPo fileRefRelationPo = new FileRefRelationPo();
        Long snowId = Long.valueOf(snowflakeShardingKeyGenerator.generateKey().toString());
        fileRefRelationPo.setId(snowId);
        Date nowDate = DateViewUtils.getNowDate();
        fileRefRelationPo.setGmtCreate(nowDate);
        fileRefRelationPo.setGmtModified(nowDate);
        fileRefRelationPo.setStatus(1);
        fileRefRelationPo.setFileRefId(fileRefId);
        fileRefRelationPo.setAppId(appId);
        return fileRefRelationPo;
    }

    @Override
    public Boolean deleteRefRelation(List<Long> fileRefIds, Integer appId) {
        int result = fileRefRelationPoMapper.deleteByRefIdAndAppId(fileRefIds, appId);
        return result > 0;
    }

    @Override
    public List<Long> selectUsedFileRefIds(List<Long> fileRefIds) {
        return fileRefRelationPoMapper.selectUsedFileRefIds(fileRefIds);
    }
}
