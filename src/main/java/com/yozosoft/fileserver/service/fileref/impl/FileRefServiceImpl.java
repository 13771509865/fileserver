package com.yozosoft.fileserver.service.fileref.impl;

import com.yozosoft.fileserver.common.utils.DateViewUtils;
import com.yozosoft.fileserver.dao.YozoFileRefPoMapper;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author zhoufeng
 * @description fileRef Service
 * @create 2020-05-12 15:16
 **/
@Service("fileRefServiceImpl")
public class FileRefServiceImpl implements IFileRefService {

    @Autowired
    private YozoFileRefPoMapper yozoFileRefPoMapper;

    @Autowired
    private SnowflakeShardingKeyGenerator snowflakeShardingKeyGenerator;

    @Override
    public YozoFileRefPo getFileRefByMd5(String fileMd5) {
        YozoFileRefPo yozoFileRefPo = yozoFileRefPoMapper.selectByFileMd5(fileMd5);
        return yozoFileRefPo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertFileRefPo(YozoFileRefPo yozoFileRefPo) {
        try {
            int insertNum = yozoFileRefPoMapper.insertSelective(yozoFileRefPo);
            return insertNum > 0;
        } catch (DuplicateKeyException duplicateKeyException) {
            //TODO 这边可能是要改的
            return true;
        }
    }

    @Override
    public YozoFileRefPo buildYozoFileRefPo(String fileMd5, String storageUrl, Long fileSize) {
        YozoFileRefPo yozoFileRefPo = new YozoFileRefPo();
        Long snowId = Long.valueOf(snowflakeShardingKeyGenerator.generateKey().toString());
        yozoFileRefPo.setId(snowId);
        Date nowDate = DateViewUtils.getNowDate();
        yozoFileRefPo.setGmtCreate(nowDate);
        yozoFileRefPo.setGmtModified(nowDate);
        yozoFileRefPo.setStatus(1);
        yozoFileRefPo.setFileMd5(fileMd5);
        yozoFileRefPo.setStorageUrl(storageUrl);
        yozoFileRefPo.setFileSize(fileSize);
        return yozoFileRefPo;
    }
}
