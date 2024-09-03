package com.example.stock_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.example.common", "com.example.stock_system"})
public class StockSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockSystemApplication.class, args);
    }

}
