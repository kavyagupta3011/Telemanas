package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.AutoCall;
import com.telemanas.eventconsumer.model.AutoCallInput;
import com.telemanas.eventconsumer.repository.AutoCallRepository;

@Service
public class AutoCallConsumer {

    private final AutoCallRepository autoCallRepository;

    public AutoCallConsumer(AutoCallRepository autoCallRepository) {
        this.autoCallRepository = autoCallRepository;
    }

    @KafkaListener(
        topics = "agent-autocall-events", 
        groupId = "telemanas-autocall-group",
        containerFactory = "autoCallKafkaListenerContainerFactory"
    )
    public void consumeAutoCallEvent(AutoCallInput input) {

        if (input.getId() == null) {
            System.err.println("Dropped event: Missing AutoCall ID.");
            return;
        }

        // Fetch existing record by ID to update, or create a new one if it doesn't exist
        AutoCall autoCall = autoCallRepository.findById(input.getId())
                .orElseGet(() -> {
                    AutoCall newAutoCall = new AutoCall();
                    newAutoCall.setId(input.getId());
                    return newAutoCall;
                });

        // Direct 1-to-1 mapping of fields
        autoCall.setSessionId(input.getSessionId());
        autoCall.setAutoCallOnStartTime(input.getAutoCallOnStartTime());
        autoCall.setAutoCallOnEndTime(input.getAutoCallOnEndTime());
        autoCall.setAutoCallOffEndTime(input.getAutoCallOffEndTime());
        autoCall.setAutoCallStartReason(input.getAutoCallStartReason());
        autoCall.setEndReason(input.getEndReason());
        autoCall.setCampaignId(input.getCampaignId());

        // Save or update the record in Postgres
        autoCallRepository.save(autoCall);

        System.out.println("Processed AutoCall event for ID: " + input.getId());
    }
}