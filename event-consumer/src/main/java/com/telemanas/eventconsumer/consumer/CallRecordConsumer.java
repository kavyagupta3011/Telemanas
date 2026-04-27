package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.CallRecord;
import com.telemanas.eventconsumer.model.CallRecordInput;
import com.telemanas.eventconsumer.repository.CallRecordRepository;

// Service responsible for consuming live call events from Kafka and processing them (saving/updating call records in the database).
@Service
public class CallRecordConsumer {

    private final CallRecordRepository callRepository;

    public CallRecordConsumer(CallRecordRepository callRepository) {
        this.callRepository = callRepository;
    }

    // Kafka listener to topic "live-call-events" with group ID "telemanas-call-group". It uses a specific container factory for deserialization.
    @KafkaListener(
        topics = "live-call-events", 
        groupId = "telemanas-call-group",
        containerFactory = "callKafkaListenerContainerFactory"
    )

    public void consumeLiveCallEvent(CallRecordInput input) {
        if (input.getCallId() == null) {
            System.err.println("Dropped live call event: Missing call_id");
            return;
        }

        CallRecord record = callRepository.findById(input.getCallId())
                .orElseGet(CallRecord::new);

        record.setCallId(input.getCallId());

        if (input.getCrtObjectId() != null)
            record.setCrtObjectId(input.getCrtObjectId());

        if (input.getCallLegId() != null)
            record.setCallLegId(input.getCallLegId());

        if (input.getCampaignId() != null)
            record.setCampaignId(input.getCampaignId());

        if (input.getIsOutbound() != null)
            record.setIsOutbound(input.getIsOutbound());

        if (input.getCallType() != null)
            record.setCallType(input.getCallType());

        record.setCurrentState(input.getEventType());

        if (input.getSystemDisposition() != null)
            record.setSystemDisposition(input.getSystemDisposition());

        if (input.getCallResult() != null)
            record.setCallResult(input.getCallResult());

        if (input.getCallOriginateTime() != null)
            record.setCallOriginateTime(input.getCallOriginateTime());

        // Incremental updates
        if (input.getIvrTime() != null)
            record.setIvrTime(input.getIvrTime());

        if (input.getRingingTime() != null)
            record.setRingingTime(input.getRingingTime());

        if (input.getTalkTime() != null)
            record.setTalkTime(input.getTalkTime());

        // FINAL STATE
        if ("CALL_ENDED".equalsIgnoreCase(input.getEventType())) {

            if (input.getCallEndTime() != null)
                record.setCallEndTime(input.getCallEndTime());

            if (input.getHangupCauseDescription() != null)
                record.setHangupCauseDescription(input.getHangupCauseDescription());

            if (input.getHangupOnHold() != null)
                record.setHangupOnHold(input.getHangupOnHold());

            if (input.getSetupTime() != null)
                record.setSetupTime(input.getSetupTime());

            if (input.getHoldTime() != null)
                record.setHoldTime(input.getHoldTime());
        }

        callRepository.save(record);

        System.out.println("Live Update: Call " + input.getCallId() +
                " → " + input.getEventType());
    }
}