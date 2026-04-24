package com.telemanas.eventproducer.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.CmCdrEvent;
import com.telemanas.eventproducer.service.CmCdrProducerService;

// REST controller responsible for handling CM CDR events and sending them to Kafka via the CmCdrProducerService.
@RestController
@RequestMapping("/api/cm-cdr") // Base path for all CM CDR related endpoints
public class CmCdrController {

    private final CmCdrProducerService producer;

    public CmCdrController(CmCdrProducerService producer) {
        this.producer = producer;
    }

    // Endpoint to publish a CM CDR event to Kafka.
    @PostMapping
    public ResponseEntity<String> publish(@RequestBody CmCdrEvent event) {
        // request body is deserialized into a CmCdrEvent object, which is then sent to Kafka using the producer service.
        if (event.getStartTime() == null) {
            event.setStartTime(Instant.now());
        }
        producer.send(event);
        
        return ResponseEntity.ok("CM CDR event sent");
    }
}