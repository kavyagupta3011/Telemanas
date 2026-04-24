package com.telemanas.eventproducer.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.UserDisposition;
import com.telemanas.eventproducer.service.UserDispositionProducerService;

// REST controller responsible for handling user disposition events and sending them to Kafka via the UserDispositionProducerService.
@RestController
@RequestMapping("/api/user-disposition") // Base path for all user disposition related endpoints
public class UserDispositionController {

    private final UserDispositionProducerService producer;

    public UserDispositionController(UserDispositionProducerService producer) {
        this.producer = producer;
    }

    // Endpoint to publish a user disposition event to Kafka.
    @PostMapping
    public ResponseEntity<String> publish(@RequestBody UserDisposition event) {
        // request body is deserialized into a UserDisposition object, which is then sent to Kafka using the producer service.
        event.setEventTimestamp(Instant.now());
        producer.send(event);

        return ResponseEntity.ok("User disposition event sent");
    }
}