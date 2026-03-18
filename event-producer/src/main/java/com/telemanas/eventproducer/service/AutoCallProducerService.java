package com.telemanas.eventproducer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.telemanas.eventproducer.model.AutoCallEvent;

@Service
public class AutoCallProducerService {
    
    private final KafkaTemplate<String, AutoCallEvent> kafkaTemplate;

    public AutoCallProducerService(KafkaTemplate<String, AutoCallEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(AutoCallEvent event) {
        kafkaTemplate.send("agent-autocall-events",
                event.getSessionId(),
                event);
    }

   
}
