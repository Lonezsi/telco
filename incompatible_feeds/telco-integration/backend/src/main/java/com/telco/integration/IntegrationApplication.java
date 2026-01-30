package com.telco.integration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.telco.integration.service.ProductIntegrationService;

@SpringBootApplication
public class IntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationApplication.class, args);
	}

	@Bean
	CommandLineRunner startIngestion(ProductIntegrationService service) {
		return args -> {
			System.out.println("///Start manual ingestion...///");
			service.ingestAndMerge();
			System.out.println("///Ingestion completed.///");
		};
	}

}
