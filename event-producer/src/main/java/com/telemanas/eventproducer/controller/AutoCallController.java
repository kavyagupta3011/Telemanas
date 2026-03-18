package com.telemanas.eventproducer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.AutoCallEvent;
import com.telemanas.eventproducer.service.AutoCallProducerService;

@RestController
@RequestMapping("/api/auto-call")
public class AutoCallController {

    private final AutoCallProducerService producer;

    public AutoCallController(AutoCallProducerService producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody AutoCallEvent event) {
        producer.send(event);
        return ResponseEntity.ok("Auto call event sent");
    }
}