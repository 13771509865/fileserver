package com.yozosoft.fileserver.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author zhoufeng
 * @description druid 配置
 * @create 2019-10-16 10:23
 **/
@Configuration
public class DruidConfig {

    @Bean("druidDataSource0")
    @ConfigurationProperties("spring.datasource.druid.one")
    public DataSource dataSourceOne() {
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        return druidDataSource;
    }
}
