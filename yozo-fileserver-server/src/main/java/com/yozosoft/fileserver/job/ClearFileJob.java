package com.yozosoft.fileserver.job;

import com.yozosoft.fileserver.common.constants.TimeConstant;
import com.yozosoft.fileserver.common.helper.ClearFileHelper;
import com.yozosoft.fileserver.common.utils.DateViewUtils;
import com.yozosoft.fileserver.config.FileServerProperties;
import com.yozosoft.fileserver.service.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zhoufeng
 * @description 刪除临时文件定时任务
 * @create 2020-05-21 14:58
 **/
@Component
@Slf4j
public class ClearFileJob {

    @Autowired
    private ClearFileHelper clearFileHelper;

    @Autowired
    private FileServerProperties fileServerProperties;

    @Autowired
    private RedisService<String> redisService;

    private static final String ClearJobLockName = "clearJobLock";

    private static final String ClearEmptyDirJobLockName = "clearEmptyDirJobLock";

    private static final String ClearChunkFileJobLockName = "clearChunkFileJobLock";

     /*"0 0 12 * * ?" 每天中午十二点触发 "0 15 10 ? * *" 每天早上10：15触发 "0 15 10 * * ?"
    每天早上10：15触发 "0 15 10 * * ? *" 每天早上10：15触发 "0 15 10 * * ? 2005" 2005年的每天早上10：15触发
		"0 * 14 * * ?" 每天从下午2点开始到2点59分每分钟一次触发 "0 0/5 14 * * ?" 每天从下午2点开始到2：55分结束每5分钟一次触发
		"0 0/5 14,18 * * ?" 每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发 "0 0-5 14 * * ?"
    每天14:00至14:05每分钟一次触发 "0 10,44 14 ? 3 WED" 三月的每周三的14：10和14：44触发 "0 15 10 ?
            * MON-FRI" 每个周一、周二、周三、周四、周五的10：15触发 "0 15 10 15 * ?" 每月15号的10：15触发 "0 15
            10 L * ?" 每月的最后一天的10：15触发 "0 15 10 ? * 6L" 每月最后一个周五的10：15触发 "0 15 10 ? *
            6L" 每月最后一个周五的10：15触发 "0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月最后一个周五的10：15触发
            "0 15 10 ? * 6#3" 每月的第三个周五的10：15触发*/

    /**
     * @description 自动清理临时文件
     * @author zhoufeng
     * @date 2019/2/11
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void clearTempFile() {
        //不支持redis集群模式
        boolean flag = redisService.setnx(ClearJobLockName, DateViewUtils.getNowFull(), 12 * TimeConstant.SECOND_OF_HOUR);
        if (flag) {
            String clearPath = fileServerProperties.getTempPath();
            //目前是定的一天一清理
            clearFileHelper.clearFile(clearPath, TimeConstant.MILLISECOND_OF_DAY);
            redisService.delete(ClearJobLockName);
        }
    }

    /**
     * @description 自动清理分片临时文件
     * @author zhoufeng
     * @date 2020/11/13
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void clearChunkFile() {
        Integer clearChunkDay = fileServerProperties.getClearChunkDay() == null ? 7 : fileServerProperties.getClearChunkDay();
        //不支持redis集群模式
        boolean flag = redisService.setnx(ClearChunkFileJobLockName, DateViewUtils.getNowFull(), 12 * TimeConstant.SECOND_OF_HOUR);
        if (flag) {
            String clearPath = fileServerProperties.getChunkPath();
            //目前是定的七天一清理
            clearFileHelper.clearFile(clearPath, TimeConstant.MILLISECOND_OF_DAY * clearChunkDay);
            redisService.delete(ClearChunkFileJobLockName);
        }
    }

    /**
     * @description 自动清理空文件夹
     * @author zhoufeng
     * @date 2019/2/11
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void clearEmptyDir() {
        //不支持redis集群模式
        boolean flag = redisService.setnx(ClearEmptyDirJobLockName, DateViewUtils.getNowFull(), 12 * TimeConstant.SECOND_OF_HOUR);
        if (flag) {
            String clearPath = fileServerProperties.getDownloadRoot();
            //目前是定的一天一清理
            clearFileHelper.clearEmptyDir(clearPath);
            redisService.delete(ClearEmptyDirJobLockName);
        }
    }
}
