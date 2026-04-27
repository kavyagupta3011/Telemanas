package com.telemanas.eventproducer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.telemanas.eventproducer.model.UserDisposition;

// Service responsible for sending user disposition events to Kafka.
@Service
public class UserDispositionProducerService {

    private final KafkaTemplate<String, UserDisposition> kafkaTemplate;

    public UserDispositionProducerService(KafkaTemplate<String, UserDisposition> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(UserDisposition event) {

        kafkaTemplate.send( "user-disposition-events", event.getId(), event);
    }
}