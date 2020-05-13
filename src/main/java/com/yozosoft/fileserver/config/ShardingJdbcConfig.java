package com.yozosoft.fileserver.config;

import lombok.extern.slf4j.Slf4j;
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
        properties.setProperty("worker.id", fileServerProperties.getWorkId());
        snowflakeShardingKeyGenerator.setProperties(properties);
        return snowflakeShardingKeyGenerator;
    }
}
