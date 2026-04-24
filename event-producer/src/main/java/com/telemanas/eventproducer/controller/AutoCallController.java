package com.telemanas.eventproducer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.AutoCallEvent;
import com.telemanas.eventproducer.service.AutoCallProducerService;

// REST controller responsible for handling auto call events and sending them to Kafka via the AutoCallProducerService.
@RestController
@RequestMapping("/api/auto-call") // Base path for all auto call related endpoints
public class AutoCallController {

    private final AutoCallProducerService producer;

    public AutoCallController(AutoCallProducerService producer) {
        this.producer = producer;
    }

    // Endpoint to publish an auto call event to Kafka.
    @PostMapping
    public ResponseEntity<String> publish(@RequestBody AutoCallEvent event) {
        // request body is deserialized into an AutoCallEvent object, which is then sent to Kafka using the producer service.
        producer.send(event);
        return ResponseEntity.ok("Auto call event sent");
    }
}