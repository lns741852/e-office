package com.cgpt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.cgpt.mapper")
@EnableScheduling
public class YebServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(YebServerApplication.class,args);
    }
}
