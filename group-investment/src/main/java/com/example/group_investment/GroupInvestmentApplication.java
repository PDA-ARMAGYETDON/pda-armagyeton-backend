package com.example.group_investment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.common", "com.example.group_investment"})
public class GroupInvestmentApplication {


	public static void main(String[] args) {
		SpringApplication.run(GroupInvestmentApplication.class, args);
	}
}
