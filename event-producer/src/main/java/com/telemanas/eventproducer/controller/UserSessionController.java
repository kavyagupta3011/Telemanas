package com.telemanas.eventproducer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telemanas.eventproducer.model.UserSessionEvent;
import com.telemanas.eventproducer.service.UserSessionProducerService;

@RestController
@RequestMapping("/api/user-sessions")
public class UserSessionController {

    private final UserSessionProducerService producer;

    public UserSessionController(UserSessionProducerService producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody UserSessionEvent event) {

        producer.send(event);
        return ResponseEntity.ok("User session event sent");
    }
}
