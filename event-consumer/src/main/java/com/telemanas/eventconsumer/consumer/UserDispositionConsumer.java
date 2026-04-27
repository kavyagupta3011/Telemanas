package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.UserDisposition;
import com.telemanas.eventconsumer.model.UserDispositionInput;
import com.telemanas.eventconsumer.repository.UserDispositionRepository;

// Service responsible for consuming user disposition events from Kafka and processing them (saving/updating user disposition records in the database).
@Service
public class UserDispositionConsumer {

    private final UserDispositionRepository userDispositionRepository;

    public UserDispositionConsumer(UserDispositionRepository userDispositionRepository) {
        this.userDispositionRepository = userDispositionRepository;
    }

    // Kafka listener to topic "user-disposition-events" with group ID "telemanas-user-disposition-group". It uses a specific container factory for deserialization.
    @KafkaListener(
        topics = "user-disposition-events",
        groupId = "telemanas-user-disposition-group",
        containerFactory = "userDispositionKafkaListenerContainerFactory"
    )

    public void consumeUserDispositionEvent(UserDispositionInput input) {

        if (input.getId() == null) {
            System.err.println("Dropped event: Missing UserDisposition ID.");
            return;
        }

        UserDisposition record = userDispositionRepository.findById(input.getId())
                .orElseGet(() -> {
                    UserDisposition newRecord = new UserDisposition();
                    newRecord.setId(input.getId());
                    return newRecord;
                });

    
        if (input.getCallId() != null)
            record.setCallId(input.getCallId());

        if (input.getCallLegId() != null)
            record.setCallLegId(input.getCallLegId());

        if (input.getDateAdded() != null)
            record.setDateAdded(input.getDateAdded());

        if (input.getUserDispositionTime() != null)
            record.setUserDispositionTime(input.getUserDispositionTime());

        if (input.getTransferTime() != null)
            record.setTransferTime(input.getTransferTime());

        if (input.getTransferTo() != null)
            record.setTransferTo(input.getTransferTo());

        if (input.getDispositionClass() != null)
            record.setDispositionClass(input.getDispositionClass());

        if (input.getDispositionCode() != null)
            record.setDispositionCode(input.getDispositionCode());

        if (input.getUserId() != null)
            record.setUserId(input.getUserId());

        if (input.getSessionId() != null)
            record.setSessionId(input.getSessionId());

        if (input.getWrapTime() != null)
            record.setWrapTime(input.getWrapTime());

        if (input.getTalkTime() != null)
            record.setTalkTime(input.getTalkTime());

        if (input.getWorking() != null)
            record.setWorking(input.getWorking());

        if (input.getDisposedByCrm() != null)
            record.setDisposedByCrm(input.getDisposedByCrm());

        if (input.getAutoCallOnTime() != null)
            record.setAutoCallOnTime(input.getAutoCallOnTime());

        if (input.getAutoCallOffTime() != null)
            record.setAutoCallOffTime(input.getAutoCallOffTime());

        if (input.getUserConnectedTime() != null)
            record.setUserConnectedTime(input.getUserConnectedTime());

        if (input.getUserDisconnectedTime() != null)
            record.setUserDisconnectedTime(input.getUserDisconnectedTime());

        if (input.getCampaignId() != null)
            record.setCampaignId(input.getCampaignId());

        if (input.getAssociationType() != null)
            record.setAssociationType(input.getAssociationType());

        if (input.getCustomerHoldTime() != null)
            record.setCustomerHoldTime(input.getCustomerHoldTime());

        if (input.getHoldTime() != null)
            record.setHoldTime(input.getHoldTime());

        userDispositionRepository.save(record);

        System.out.println("Processed UserDisposition event for ID: " + input.getId());
    }
}