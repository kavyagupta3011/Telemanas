package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.UserDisposition;
import com.telemanas.eventconsumer.model.UserDispositionInput;
import com.telemanas.eventconsumer.repository.UserDispositionRepository;

@Service
public class UserDispositionConsumer {

    private final UserDispositionRepository userDispositionRepository;

    public UserDispositionConsumer(UserDispositionRepository userDispositionRepository) {
        this.userDispositionRepository = userDispositionRepository;
    }

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

        // Fetch existing record OR create new (UPSERT logic)
        UserDisposition record = userDispositionRepository.findById(input.getId())
                .orElseGet(() -> {
                    UserDisposition newRecord = new UserDisposition();
                    newRecord.setId(input.getId());
                    return newRecord;
                });

        // --- Field Mapping ---
        record.setCallId(input.getCallId());
        record.setCallLegId(input.getCallLegId());
        record.setDateAdded(input.getDateAdded());
        record.setUserDispositionTime(input.getUserDispositionTime());
        record.setTransferTime(input.getTransferTime());
        record.setTransferTo(input.getTransferTo());
        record.setDispositionClass(input.getDispositionClass());
        record.setDispositionCode(input.getDispositionCode());
        record.setUserId(input.getUserId());
        record.setSessionId(input.getSessionId());
        record.setWrapTime(input.getWrapTime());
        record.setTalkTime(input.getTalkTime());
        record.setWorking(input.getWorking());
        record.setDisposedByCrm(input.getDisposedByCrm());
        record.setAutoCallOnTime(input.getAutoCallOnTime());
        record.setAutoCallOffTime(input.getAutoCallOffTime());
        record.setUserConnectedTime(input.getUserConnectedTime());
        record.setUserDisconnectedTime(input.getUserDisconnectedTime());
        record.setCampaignId(input.getCampaignId());
        record.setAssociationType(input.getAssociationType());
        record.setCustomerHoldTime(input.getCustomerHoldTime());
        record.setHoldTime(input.getHoldTime());

        // Save (insert or update)
        userDispositionRepository.save(record);

        System.out.println("Processed UserDisposition event for ID: " + input.getId());
    }
}