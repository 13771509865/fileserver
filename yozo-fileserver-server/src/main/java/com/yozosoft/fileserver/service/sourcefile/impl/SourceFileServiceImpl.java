package com.yozosoft.fileserver.service.sourcefile.impl;

import com.yozosoft.fileserver.common.constants.StorageConstant;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import com.yozosoft.fileserver.common.utils.Md5Utils;
import com.yozosoft.fileserver.config.FileServerProperties;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.model.po.YozoFileRefPo;
import com.yozosoft.fileserver.service.fileref.IFileRefService;
import com.yozosoft.fileserver.service.refrelation.IRefRelationService;
import com.yozosoft.fileserver.service.sourcefile.ISourceFileService;
import com.yozosoft.fileserver.service.storage.IStorageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-22 16:02
 **/
@Service("sourceFileServiceImpl")
@Slf4j
public class SourceFileServiceImpl implements ISourceFileService {

    @Autowired
    private IRefRelationService iRefRelationService;

    @Autowired
    private IFileRefService iFileRefService;

    @Autowired
    private IStorageManager iStorageManager;

    @Autowired
    private FileServerProperties fileServerProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IResult<String> checkAndDeleteFile(List<YozoFileRefPo> yozoFileRefPos, List<Long> fileRefIds, Integer appId) {
        //TODO 这种删除要不要判断
        Boolean relationDelete = iRefRelationService.deleteRefRelation(fileRefIds, appId);
        List<Long> usedFileRefIds = iRefRelationService.selectUsedFileRefIds(fileRefIds);
        List<Long> deleteFileRefIds = getDeleteFileRefIds(fileRefIds, usedFileRefIds);
        iFileRefService.deleteByIds(deleteFileRefIds);
        //删除物理文件
        for (Long fileRefId : deleteFileRefIds) {
            String storageUrl = getStorageUrl(yozoFileRefPos, fileRefId);
            IResult<String> deleteResult = iStorageManager.deleteFile(storageUrl);
            if (!deleteResult.isSuccess()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DefaultResult.failResult(deleteResult.getMessage());
            }
        }
        return DefaultResult.successResult();
    }

    @Override
    public String getChunkFolderPath(String fileMd5) {
        String chunkPath = fileServerProperties.getChunkPath();
        File file = new File(chunkPath, fileMd5);
        return file.getAbsolutePath();
    }

    @Override
    public IResult<String> storageChunkFile(String parentPath, Integer chunk, MultipartFile multipartFile) {
        chunk = chunk == null ? 0 : chunk;
        File chunkFile = new File(parentPath, chunk + "");
        try {
            multipartFile.transferTo(chunkFile);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存切片文件失败,path为:" + chunkFile.getAbsolutePath() + ",chunk为:" + chunk, e);
            return DefaultResult.failResult(EnumResultCode.E_STORAGE_CHUNK_FILE_FAIL.getInfo());
        }
        return DefaultResult.successResult();
    }

    @Override
    public IResult<File> mergeChunkFile(String fileMd5, Integer chunks, String fileName) {
        String chunkFolderPath = getChunkFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFolderPath);
        if (!chunkFileFolder.exists() || !chunkFileFolder.isDirectory()) {
            return DefaultResult.failResult(EnumResultCode.E_MERGE_CHUNK_FAIL.getInfo());
        }
        File[] files = chunkFileFolder.listFiles();
        if (files.length != chunks) {
            return DefaultResult.failResult(EnumResultCode.E_MERGE_CHUNK_FAIL.getInfo());
        }
        File mergeFile = getMergeFolderPath(fileMd5, fileName);
        Boolean mergeResult = mergeFile(Arrays.asList(files), mergeFile);
        if(!mergeResult){
            return DefaultResult.failResult(EnumResultCode.E_MERGE_CHUNK_FAIL.getInfo());
        }
        //检查合并文件的md5
        Boolean checkMd5 = checkMergeMd5(mergeFile, fileMd5);
        if(!checkMd5){
            return DefaultResult.failResult(EnumResultCode.E_MERGE_CHUNK_FAIL.getInfo());
        }
        return DefaultResult.successResult(mergeFile);
    }

    private Boolean checkMergeMd5(File mergeFile, String fileMd5){
        try{
            String md5 = Md5Utils.getMD5(mergeFile);
            return fileMd5.toLowerCase().equals(md5);
        }catch (Exception e){
            e.printStackTrace();
            log.error("计算mergeFile的Md5异常");
            return false;
        }
    }

    public File getMergeFolderPath(String fileMd5, String fileName) {
        String chunkPath = fileServerProperties.getChunkPath();
        File file = new File(chunkPath, StorageConstant.MERGE_FOLDER + "/" + fileMd5 + "/" + fileName);
        return file;
    }

    private Boolean mergeFile(List<File> chunkFileList, File mergeFile) {
        try {
            // 有就先删除
            if (mergeFile.exists()) {
                mergeFile.delete();
            }
            File parentFile = mergeFile.getParentFile();
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }
            mergeFile.createNewFile();
            // 排序
            Collections.sort(chunkFileList, (o1, o2) -> {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    return 1;
                }
                return -1;
            });
            byte[] b = new byte[2048];
            RandomAccessFile writeFile = new RandomAccessFile(mergeFile, "rw");
            for (File chunkFile : chunkFileList) {
                RandomAccessFile readFile = new RandomAccessFile(chunkFile, "r");
                int len = -1;
                while ((len = readFile.read(b)) != -1) {
                    writeFile.write(b, 0, len);
                }
                readFile.close();
            }
            writeFile.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("真实合并文件失败", e);
            return false;
        }
    }


    private List<Long> getDeleteFileRefIds(List<Long> fileRefIds, List<Long> usedFileRefIds) {
        if (usedFileRefIds == null || usedFileRefIds.isEmpty()) {
            return fileRefIds;
        }
        //深拷贝
        List<Long> result = new ArrayList<>();
        Collections.addAll(result, new Long[fileRefIds.size()]);
        Collections.copy(result, fileRefIds);
        result.removeAll(usedFileRefIds);
        return result;
    }

    private String getStorageUrl(List<YozoFileRefPo> yozoFileRefPos, Long fileRefId) {
        for (YozoFileRefPo yozoFileRefPo : yozoFileRefPos) {
            if (fileRefId.equals(yozoFileRefPo.getId())) {
                return yozoFileRefPo.getStorageUrl();
            }
        }
        return null;
    }
}
