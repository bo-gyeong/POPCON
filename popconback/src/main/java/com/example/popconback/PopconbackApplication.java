package com.example.popconback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PopconbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(PopconbackApplication.class, args);
	}

}
