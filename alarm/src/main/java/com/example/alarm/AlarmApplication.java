package com.example.alarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.common", "com.example.alarm"})
public class AlarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlarmApplication.class, args);
    }

}
