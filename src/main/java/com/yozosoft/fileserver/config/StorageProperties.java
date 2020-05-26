package com.yozosoft.fileserver.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-13 14:54
 **/
@Component
@ConfigurationProperties(prefix = "yfs.storage")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RefreshScope
public class StorageProperties {

    private String localRootPath;

    private String endPoint;

    private String accesskey;

    private String secretKey;

    private String bucketName;
}
