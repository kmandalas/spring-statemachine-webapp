package com.example.demolition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.demolition.entity")  // Ensure Hibernate scans this package
@EnableJpaRepositories(basePackages = "com.example.demolition.repository")  // Ensure repositories are picked up
public class DemolitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemolitionApplication.class, args);
    }

}
