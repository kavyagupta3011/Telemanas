package com.telemanas.eventproducer.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.telemanas.eventproducer.model.UserDisposition;
import com.telemanas.eventproducer.service.UserDispositionProducerService;

@RestController
@RequestMapping("/api/user-disposition")
public class UserDispositionController {

    private final UserDispositionProducerService producer;

    public UserDispositionController(UserDispositionProducerService producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody UserDisposition event) {

        // Set timestamp (same pattern as yours)
        event.setEventTimestamp(Instant.now());

        // Send to Kafka
        producer.send(event);

        return ResponseEntity.ok("User disposition event sent");
    }
}