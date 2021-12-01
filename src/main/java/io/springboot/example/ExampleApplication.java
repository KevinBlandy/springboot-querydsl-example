package io.springboot.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * 
 * spring-data-jpa + querydsl 
 * 
 * 
 * @author KevinBlandy
 *
 */
@EnableJpaRepositories(basePackages = { "io.springboot.example.repository" })
@EntityScan(basePackages = { "io.springboot.example.entity" })
@SpringBootApplication
public class ExampleApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}
}
