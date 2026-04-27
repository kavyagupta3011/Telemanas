package com.telemanas.eventproducer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.telemanas.eventproducer.model.CallEvent;

// Service responsible for sending live call events to Kafka.
@Service
public class CallProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CallProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(CallEvent event) {
        kafkaTemplate.send("live-call-events", event.getCallId(), event); 
    }
}