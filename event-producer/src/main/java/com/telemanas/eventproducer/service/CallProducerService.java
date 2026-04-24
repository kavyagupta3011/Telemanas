package com.telemanas.eventproducer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.telemanas.eventproducer.model.CallEvent;

// Service responsible for sending live call events to Kafka.
@Service
public class CallProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "live-call-events";

    public CallProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(CallEvent event) {
        // We use crtObjectId as the Kafka key so all events for the same call stay in order
        kafkaTemplate.send(TOPIC, event.getCrtObjectId(), event); 
    }
}