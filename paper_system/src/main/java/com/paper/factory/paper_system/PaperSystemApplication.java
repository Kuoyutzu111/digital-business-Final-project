package com.paper.factory.paper_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PaperSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaperSystemApplication.class, args);
	}

}
