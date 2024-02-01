package com.care4u;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Care4UApplication {

	public static void main(String[] args) {
		SpringApplication.run(Care4UApplication.class, args);
	}

}
