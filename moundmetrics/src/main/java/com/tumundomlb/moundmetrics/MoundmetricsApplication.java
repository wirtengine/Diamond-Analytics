package com.tumundomlb.moundmetrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoundmetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoundmetricsApplication.class, args);
    }
}