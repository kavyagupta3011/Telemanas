package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.AutoCall;
import com.telemanas.eventconsumer.model.AutoCallInput;
import com.telemanas.eventconsumer.repository.AutoCallRepository;

// Service responsible for consuming auto call events from Kafka and processing them (saving to the database).
@Service
public class AutoCallConsumer {

    private final AutoCallRepository autoCallRepository;

    public AutoCallConsumer(AutoCallRepository autoCallRepository) {
        this.autoCallRepository = autoCallRepository;
    }

    // Kafka listener to topic "agent-autocall-events" with group ID "telemanas-autocall-group". It uses a specific container factory for deserialization.
    @KafkaListener(
        topics = "agent-autocall-events", 
        groupId = "telemanas-autocall-group",
        containerFactory = "autoCallKafkaListenerContainerFactory"
    )
    
    public void consumeAutoCallEvent(AutoCallInput input) {

        if (input.getSessionId() == null) {
            System.err.println("Dropped event: Missing session ID.");
            return;
        }

        AutoCall autoCall = autoCallRepository
        .findBySessionId(input.getSessionId())
        .orElseGet(AutoCall::new);

        autoCall.setSessionId(input.getSessionId());
        autoCall.setCampaignId(input.getCampaignId());

        if (input.getAutoCallOnStartTime() != null) {
            autoCall.setAutoCallOnStartTime(input.getAutoCallOnStartTime());
        }

        if (input.getAutoCallOnEndTime() != null) {
            autoCall.setAutoCallOnEndTime(input.getAutoCallOnEndTime());
        }

        if (input.getAutoCallOffEndTime() != null) {
            autoCall.setAutoCallOffEndTime(input.getAutoCallOffEndTime());
        }

        if (input.getAutoCallStartReason() != null) {
            autoCall.setAutoCallStartReason(input.getAutoCallStartReason());
        }

        if (input.getEndReason() != null) {
            autoCall.setEndReason(input.getEndReason());
        }

        autoCallRepository.save(autoCall);

        System.out.println("Processed AutoCall event for session: " + input.getSessionId());
    }
}