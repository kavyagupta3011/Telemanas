package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.CmCdr;
import com.telemanas.eventconsumer.model.CmCdrInput;
import com.telemanas.eventconsumer.repository.CmCdrRepository;

// Service responsible for consuming CM CDR events from Kafka and processing them (saving/updating CDR records in the database).
@Service
public class CmCdrConsumer {

    private final CmCdrRepository repository;

    public CmCdrConsumer(CmCdrRepository repository) {
        this.repository = repository;
    }

    // Kafka listener to topic "cm-cdr-events" with group ID "telemanas-cdr-group". It uses a specific container factory for deserialization.
    @KafkaListener(
        topics = "cm-cdr-events",
        groupId = "telemanas-cdr-group",
        containerFactory = "cmCdrKafkaListenerContainerFactory"
    )

    public void consume(CmCdrInput input) {

        if (input.getCallLegId() == null) {
            System.err.println("Dropped CDR event: Missing call_leg_id");
            return;
        }

        CmCdr record = repository.findById(input.getCallLegId())
                .orElseGet(() -> {
                    CmCdr r = new CmCdr();
                    r.setCallLegId(input.getCallLegId());
                    return r;
                });


        if (input.getHangupCause() != null)
            record.setHangupCause(input.getHangupCause());

        if (input.getHangupCauseCode() != null)
            record.setHangupCauseCode(input.getHangupCauseCode());

        if (input.getSetupTime() != null)
            record.setSetupTime(input.getSetupTime());

        if (input.getRingTime() != null)
            record.setRingTime(input.getRingTime());

        if (input.getTalkTime() != null)
            record.setTalkTime(input.getTalkTime());

        if (input.getStartTime() != null)
            record.setStartTime(input.getStartTime());

        if (input.getEndTime() != null)
            record.setEndTime(input.getEndTime());

        if (input.getVoiceResourceInitializationTime() != null)
            record.setVoiceResourceInitializationTime(input.getVoiceResourceInitializationTime());

        if (input.getWhichSideHungup() != null)
            record.setWhichSideHungup(input.getWhichSideHungup());

        if (input.getInternalHangupReason() != null)
            record.setInternalHangupReason(input.getInternalHangupReason());

        repository.save(record);

        System.out.println("Processed CDR for call_leg_id: " + input.getCallLegId());
    }
}