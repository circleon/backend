package com.circleon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CircleOnApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircleOnApplication.class, args);
	}

}
