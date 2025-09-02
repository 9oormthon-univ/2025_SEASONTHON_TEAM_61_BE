package com.example.youthy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class YouthyApplication {

	public static void main(String[] args) {
		SpringApplication.run(YouthyApplication.class, args);
	}

}
