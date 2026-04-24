package com.telemanas.eventproducer.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.AgentActivityEvent;
import com.telemanas.eventproducer.service.AgentActivityProducerService;

// REST controller responsible for handling agent activity events and sending them to Kafka via the AgentActivityProducerService.
@RestController
@RequestMapping("/api/agent-activity") // Base path for all agent activity related endpoints
public class AgentActivityController {

    private final AgentActivityProducerService producer;

    public AgentActivityController(AgentActivityProducerService producer) {
        this.producer = producer;
    }

    // Endpoint to publish an agent activity event to Kafka.
    @PostMapping
    public ResponseEntity<String> publish(@RequestBody AgentActivityEvent event) {
        // request body is deserialized into an AgentActivityEvent object, which is then sent to Kafka using the producer service.
        event.setCreatedAt(Instant.now());
        producer.send(event);
        return ResponseEntity.ok("Agent activity event sent");
    }
}
