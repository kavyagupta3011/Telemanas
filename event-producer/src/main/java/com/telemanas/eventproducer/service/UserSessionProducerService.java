package com.telemanas.eventproducer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.telemanas.eventproducer.model.UserSessionEvent;

// Service responsible for sending user session events to Kafka.
@Service
public class UserSessionProducerService {

    private final KafkaTemplate<String, UserSessionEvent> kafkaTemplate;

    public UserSessionProducerService(KafkaTemplate<String, UserSessionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // key = session id
    public void send(UserSessionEvent event) {
        kafkaTemplate.send("user-session-events", event.getSessionId(), event);
    }
}

