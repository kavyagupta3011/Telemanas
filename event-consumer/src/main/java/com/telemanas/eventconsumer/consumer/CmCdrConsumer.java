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

        if (input.getCallLegId() == null) return;

        //  Find existing CDR by call leg ID, or create a brand new one if it doesn't exist yet
        CmCdr record = repository.findById(input.getCallLegId())
                .orElseGet(() -> {
                    CmCdr r = new CmCdr();
                    r.setCallLegId(input.getCallLegId());
                    return r;
                });

        // Mapping of fields 
        record.setHangupCause(input.getHangupCause());
        record.setHangupCauseCode(input.getHangupCauseCode());
        record.setSetupTime(input.getSetupTime());
        record.setRingTime(input.getRingTime());
        record.setTalkTime(input.getTalkTime());
        record.setStartTime(input.getStartTime());
        record.setEndTime(input.getEndTime());
        record.setVoiceResourceInitializationTime(input.getVoiceResourceInitializationTime());
        record.setWhichSideHungup(input.getWhichSideHungup());
        record.setInternalHangupReason(input.getInternalHangupReason());

        // Save to DB 
        repository.save(record);
    }
}