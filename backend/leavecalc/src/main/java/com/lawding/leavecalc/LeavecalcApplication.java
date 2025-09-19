package com.lawding.leavecalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LeavecalcApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeavecalcApplication.class, args);
	}

}
