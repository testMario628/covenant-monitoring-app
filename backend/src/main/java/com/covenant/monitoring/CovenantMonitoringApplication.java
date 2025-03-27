package com.covenant.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CovenantMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CovenantMonitoringApplication.class, args);
    }
}
