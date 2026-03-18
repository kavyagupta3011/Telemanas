package com.telemanas.eventproducer.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.AgentActivityEvent;
import com.telemanas.eventproducer.service.AgentActivityProducerService;

@RestController
@RequestMapping("/api/agent-activity")
public class AgentActivityController {

    private final AgentActivityProducerService producer;

    public AgentActivityController(AgentActivityProducerService producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody AgentActivityEvent event) {
        event.setCreatedAt(Instant.now());
        producer.send(event);
        return ResponseEntity.ok("Agent activity event sent");
    }
}
