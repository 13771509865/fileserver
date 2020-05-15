package com.yozosoft.fileserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-15 09:46
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalDownloadDto {

    private String fileName;

    private String storagePath;

    private FileSystemResource fileSystemResource;

    private Long fileSize;

    public LocalDownloadDto(String fileName, String storagePath) {
        this.fileName = fileName;
        this.storagePath = storagePath;
    }
}
