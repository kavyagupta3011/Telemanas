package com.telemanas.eventproducer.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.CallEvent;
import com.telemanas.eventproducer.service.CallProducerService;

@RestController
@RequestMapping("/api/live-calls")
public class CallController {

    private final CallProducerService producer;

    public CallController(CallProducerService producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody CallEvent event) {
        if (event.getEventType() == null) {
            event.setEventType("CALL");
        }
        if (event.getEventTimestamp() == null) {
            event.setEventTimestamp(Instant.now());
        }

        // Ensure we have a key for Kafka; prefer crtObjectId, fallback to callId
        if (event.getCrtObjectId() == null || event.getCrtObjectId().isEmpty()) {
            if (event.getCallId() != null) {
                event.setCrtObjectId(event.getCallId());
            }
        }

        producer.send(event);
        return ResponseEntity.ok("Live call event sent");
    }
}