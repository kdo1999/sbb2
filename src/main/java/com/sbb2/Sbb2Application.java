package com.sbb2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Sbb2Application {

	public static void main(String[] args) {
		SpringApplication.run(Sbb2Application.class, args);
	}

}
