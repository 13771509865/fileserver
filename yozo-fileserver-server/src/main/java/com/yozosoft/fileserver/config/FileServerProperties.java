package com.yozosoft.fileserver.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @author zhoufeng
 * @description 配置类
 * @create 2020-05-12 10:19
 **/
@Component
@ConfigurationProperties(prefix = "yfs")
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@RefreshScope
public class FileServerProperties {

    private Integer workId;

    @NotBlank
    private String downloadRoot;

    @NotBlank
    private String tempPath;

    @NotBlank
    private String downloadDomain;

    @NotBlank
    private String signSecret;

    @NotBlank
    private String chunkPath;

}
