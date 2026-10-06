package com.stkapi.stk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class StkApplication {

    public static void main(String[] args) {
        SpringApplication.run(StkApplication.class, args);
    }

}
