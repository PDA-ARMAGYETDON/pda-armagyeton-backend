package com.example.armagyetdon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.common", "com.example.armagyetdon"})
public class GroupInvestmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupInvestmentApplication.class, args);
    }

}
