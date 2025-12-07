package com.ouc.lenovoshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ouc.lenovoshop.mapper")
public class LenovoShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(LenovoShopApplication.class, args);
    }

}
