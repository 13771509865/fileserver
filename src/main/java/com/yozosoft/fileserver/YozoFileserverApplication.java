package com.yozosoft.fileserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.yozosoft"})
@ServletComponentScan
@MapperScan("com.yozosoft.fileserver.dao")
public class YozoFileserverApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(YozoFileserverApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(YozoFileserverApplication.class, args);
    }

}
