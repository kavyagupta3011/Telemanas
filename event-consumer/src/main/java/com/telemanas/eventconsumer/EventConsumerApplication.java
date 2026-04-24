package com.telemanas.eventconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Main application class for the Event Consumer service. This class bootstraps the Spring Boot application.
@SpringBootApplication
public class EventConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventConsumerApplication.class, args);
    }
}
