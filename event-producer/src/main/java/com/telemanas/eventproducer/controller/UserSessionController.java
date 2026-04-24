package com.telemanas.eventproducer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.UserSessionEvent;
import com.telemanas.eventproducer.service.UserSessionProducerService;


// REST controller responsible for handling user session events and sending them to Kafka via the UserSessionProducerService.
@RestController
@RequestMapping("/api/user-sessions") // Base path for all user session related endpoints
public class UserSessionController {

    private final UserSessionProducerService producer;

    public UserSessionController(UserSessionProducerService producer) {
        this.producer = producer;
    }

    // Endpoint to publish a user session event to Kafka.
    @PostMapping
    public ResponseEntity<String> publish(@RequestBody UserSessionEvent event) {
        // request body is deserialized into a UserSessionEvent object, which is then sent to Kafka using the producer service.
        producer.send(event);
        return ResponseEntity.ok("User session event sent");
    }
}
