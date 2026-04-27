package com.telemanas.eventproducer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.telemanas.eventproducer.model.CmCdrEvent;

// Service responsible for sending CM CDR events to Kafka.
@Service
public class CmCdrProducerService {

    private final KafkaTemplate<String, CmCdrEvent> kafkaTemplate;

    public CmCdrProducerService(KafkaTemplate<String, CmCdrEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(CmCdrEvent event) {

        kafkaTemplate.send( "cm-cdr-events", event.getCallLegId(),event);
    }
}