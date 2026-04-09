package com.diamondanalytics.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DiamondanalyticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiamondanalyticsApplication.class, args);
    }
}