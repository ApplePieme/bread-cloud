package com.breadme.breadcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@MapperScan("com.breadme.breadcloud.mapper")
public class BreadCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(BreadCloudApplication.class, args);
    }

}
