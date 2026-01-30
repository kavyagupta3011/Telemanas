package com.telemanas.eventproducer.service;

import com.telemanas.eventproducer.model.Event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Event> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Event event) {
        kafkaTemplate.send("call-events", event.getUserId(), event);
    }
}
