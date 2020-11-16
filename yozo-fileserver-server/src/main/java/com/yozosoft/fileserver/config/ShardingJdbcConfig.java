package com.yozosoft.fileserver.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author zhoufeng
 * @description shardingjdbc配置类
 * @create 2020-05-12 08:28
 **/
@Configuration
@Slf4j
public class ShardingJdbcConfig {

    @Autowired
    private FileServerProperties fileServerProperties;

//    @Primary
//    @Bean
//    @DependsOn("druidDataSource0")
//    public DataSource initShardingJdbcDataSource(@Qualifier("druidDataSource0") DataSource druidDataSource0) throws SQLException {
//        Map<String, DataSource> dataSourceMap = new HashMap();
//        dataSourceMap.put("ds0", druidDataSource0);
//        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
//        shardingRuleConfig.getTableRuleConfigs().add(getYozoFileRefTableRuleConfiguration());
//        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());
//        return dataSource;
//    }

    /**
     * 配置分表规则
     *
     * @return
     */
    private static TableRuleConfiguration getYozoFileRefTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("YOZOFILEREF", "ds0.YOZOFILEREF_${0..7}");
        result.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("ID", "YOZOFILEREF_${ID % 8}"));
        return result;
    }

    @Bean
    public SnowflakeShardingKeyGenerator keyGenerator() {
        SnowflakeShardingKeyGenerator snowflakeShardingKeyGenerator = new SnowflakeShardingKeyGenerator();
        Properties properties = new Properties();
        properties.setProperty("worker.id", getWorkerId());
        snowflakeShardingKeyGenerator.setProperties(properties);
        return snowflakeShardingKeyGenerator;
    }

    private String getWorkerId(){
        Integer workId = fileServerProperties.getWorkId();
        if(workId != null && workId>=0 && workId<=1024){
            return fileServerProperties.getWorkId()+"";
        }
        long workerId = 0L;
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            byte[] ipAddressByteArray = address.getAddress();
            //如果是IPV4，计算方式是遍历byte[]，然后把每个IP段数值相加得到的结果就是workerId
            if (ipAddressByteArray.length == 4) {
                for (byte byteNum : ipAddressByteArray) {
                    workerId += byteNum & 0xFF;
                }
            //如果是IPV6，计算方式是遍历byte[]，然后把每个IP段后6位（& 0B111111 就是得到后6位）数值相加得到的结果就是workerId
            } else if (ipAddressByteArray.length == 16) {
                for (byte byteNum : ipAddressByteArray) {
                    workerId += byteNum & 0B111111;
                }
            } else {
                throw new IllegalStateException("初始化获取workId失败,错误的ip地址!");
            }
        } catch (Exception e) {
            log.error("初始化获取workId失败", e);
            throw new IllegalStateException("初始化获取workId失败!");
        }
        return workerId+"";
    }
}
