package com.yozosoft.fileserver.common.utils;

import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.common.constants.SysConstant;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

/**
 * @author zhoufeng
 * @description zip工具类
 * @create 2020-05-14 18:58
 **/
public class ZipUtils {
    private final static String separator = File.separator;

    public static IResult<String> zipFile(File zipFile, File srcFile) {
        try {
            if (!srcFile.exists()) {
                return DefaultResult.failResult(EnumResultCode.E_ZIP_FILE_NOT_EXIST.getInfo());
            }
            Project project = new Project();
            FileSet fileSet = new FileSet();
            fileSet.setProject(project);
            // 判断是目录还是文件
            if (srcFile.isDirectory()) {
                fileSet.setDir(srcFile);
            } else {
                fileSet.setFile(srcFile);
            }
            Zip zip = new Zip();
            zip.setProject(project);
            zip.setDestFile(zipFile);
            zip.addFileset(fileSet);
            zip.setEncoding(SysConstant.CHARSET);
            zip.execute();
            return DefaultResult.successResult();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultResult.failResult(EnumResultCode.E_ZIP_FILE_FAIL.getInfo());
        }
    }

    public static IResult<String> unZipFile(File zipFile, File destFile) {
        try {
            if (!zipFile.exists()) {
                return DefaultResult.failResult(EnumResultCode.E_UNZIP_FILE_NOT_EXIST.getInfo());
            }
            Project project = new Project();
            Expand expand = new Expand();
            expand.setProject(project);
            expand.setTaskType("unzip");
            expand.setTaskName("unzip");
            expand.setEncoding(SysConstant.CHARSET);
            expand.setSrc(zipFile);
            expand.setDest(destFile);
            expand.execute();
            return DefaultResult.successResult();
        } catch (Exception e) {
            e.printStackTrace();
            return DefaultResult.failResult(EnumResultCode.E_UNZIP_FILE_FAIL.getInfo());
        }
    }
}
