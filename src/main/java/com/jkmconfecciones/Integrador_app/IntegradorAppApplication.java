package com.jkmconfecciones.Integrador_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IntegradorAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegradorAppApplication.class, args);
	}

}

