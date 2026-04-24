package com.telemanas.eventproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Main application class for the Event Producer service. This class bootstraps the Spring Boot application and enables scheduling for periodic tasks(simulator)
@SpringBootApplication
@EnableScheduling
public class EventProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventProducerApplication.class, args);
    }
}
