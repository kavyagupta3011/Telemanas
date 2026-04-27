package com.telemanas.eventproducer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.telemanas.eventproducer.model.AgentActivityEvent;

// Service responsible for sending agent activity events to Kafka.
@Service
public class AgentActivityProducerService {

    private final KafkaTemplate<String, AgentActivityEvent> kafkaTemplate;

    public AgentActivityProducerService(KafkaTemplate<String, AgentActivityEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // key = session id
    public void send(AgentActivityEvent event) {
        kafkaTemplate.send("agent-activity-events",event.getSessionId(),event);
    }
}
