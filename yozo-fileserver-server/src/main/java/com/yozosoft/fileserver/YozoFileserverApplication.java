package com.yozosoft.fileserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.yozosoft"})
@ServletComponentScan
@MapperScan("com.yozosoft.fileserver.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.yozosoft"})
public class YozoFileserverApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(YozoFileserverApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(YozoFileserverApplication.class, args);
    }

}
