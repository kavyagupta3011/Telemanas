package com.telemanas.eventproducer.controller;
import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.Event;
import com.telemanas.eventproducer.service.KafkaProducerService;


@RestController
@RequestMapping("/api/events")
public class EventController {

    private final KafkaProducerService producer;

    public EventController(KafkaProducerService producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody Event event) {
        if (event.getEventType() == null) {
            event.setEventType("CALL");
        }
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now());
        }

        producer.send(event);
        return ResponseEntity.ok("Event sent");
    }
}
