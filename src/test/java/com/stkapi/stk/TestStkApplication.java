package com.stkapi.stk;

import org.springframework.boot.SpringApplication;

public class TestStkApplication {

    public static void main(String[] args) {
        SpringApplication.from(StkApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
