package com.example.realtime_stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class RealtimeStockApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimeStockApplication.class, args);
	}

}
