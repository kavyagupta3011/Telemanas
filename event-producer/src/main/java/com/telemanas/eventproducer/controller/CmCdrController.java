package com.telemanas.eventproducer.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.telemanas.eventproducer.model.CmCdrEvent;
import com.telemanas.eventproducer.service.CmCdrProducerService;

@RestController
@RequestMapping("/api/cm-cdr")
public class CmCdrController {

    private final CmCdrProducerService producer;

    public CmCdrController(CmCdrProducerService producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody CmCdrEvent event) {

        // optional: set timestamp (like you did before)
        if (event.getStartTime() == null) {
            event.setStartTime(Instant.now());
        }

        producer.send(event);

        return ResponseEntity.ok("CM CDR event sent");
    }
}