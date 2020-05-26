package com.yozosoft.fileserver.service.fileref.impl;

import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.common.utils.DateViewUtils;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.dao.YozoFileRefPoMapper;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
    public IResult<Long> insertFileRefPo(YozoFileRefPo yozoFileRefPo) {
        try {
            int insertResult = yozoFileRefPoMapper.insertSelective(yozoFileRefPo);
            return insertResult > 0 ? DefaultResult.successResult(yozoFileRefPo.getId()) : DefaultResult.failResult();
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            //TODO 这边可能是要改的
            YozoFileRefPo fileRefByMd5 = getFileRefByMd5(yozoFileRefPo.getFileMd5());
            return fileRefByMd5 != null ? DefaultResult.successResult(fileRefByMd5.getId()) : DefaultResult.failResult();
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

    @Override
    public List<YozoFileRefPo> selectByCheckApp(List<Long> fileRefIds, Integer appId) {
        return yozoFileRefPoMapper.selectByCheckApp(fileRefIds, appId);
    }

    @Override
    public IResult<List<YozoFileRefPo>> buildStorageUrls(List<Long> fileRefIds, Integer appId) {
        List<YozoFileRefPo> yozoFileRefPos = selectByCheckApp(fileRefIds, appId);
        if (yozoFileRefPos == null || yozoFileRefPos.isEmpty() || fileRefIds.size() != yozoFileRefPos.size()) {
            return DefaultResult.failResult(EnumResultCode.E_APP_FILE_NUM_ILLEGAL.getInfo());
        }
        return DefaultResult.successResult(yozoFileRefPos);
    }

    @Override
    public Boolean deleteByIds(List<Long> fileRefIds) {
        int deleteResult = yozoFileRefPoMapper.deleteByIds(fileRefIds);
        return deleteResult > 0;
    }
}
