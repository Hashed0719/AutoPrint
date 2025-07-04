package com.example.autoprint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories("com.example.autoprint.repository")
@EntityScan("com.example.autoprint.model")
@EnableTransactionManagement
public class AutoprintApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoprintApplication.class, args);
    }
}
