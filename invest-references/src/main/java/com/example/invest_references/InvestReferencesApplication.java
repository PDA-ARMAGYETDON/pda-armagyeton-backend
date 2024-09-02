package com.example.invest_references;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.common", "com.example.invest_references"})
public class InvestReferencesApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestReferencesApplication.class, args);
    }

}
