package com.yozosoft.fileserver.common.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-21 13:54
 **/
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class ClearFileHelper {

    public void clearFile(String path, Long clearTime) {
        Path clearPath = Paths.get(path);
        Long currentTime = System.currentTimeMillis();
        log.info("=======================================开始清理文件=======================================");
        try {
            Files.walkFileTree(clearPath, new SimpleFileVisitor<Path>() {
                // 访问文件失败
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                // 访问文件时候触发该方法
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (currentTime - clearTime > file.toFile().lastModified()) {
                        FileUtils.deleteQuietly(file.toFile());
                    }
                    return FileVisitResult.CONTINUE;
                }

                //访问目录后触发该方法
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (dir.toFile().list().length == 0) {
                        if (dir == clearPath) {
                            return FileVisitResult.CONTINUE;
                        }
                        FileUtils.deleteQuietly(dir.toFile());
                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }
            });
        } catch (Exception e) {
            log.info("清理过期文件线程异常");
        }
    }
}
