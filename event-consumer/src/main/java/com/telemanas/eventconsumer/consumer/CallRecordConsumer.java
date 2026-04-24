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

        // Find existing call by ID, or create a brand new one if it doesn't exist yet
        CallRecord record = callRepository.findById(input.getCallId()).orElse(new CallRecord());

        // Mapping of the fields 
        record.setCallId(input.getCallId());
        record.setCrtObjectId(input.getCrtObjectId());
        record.setCallLegId(input.getCallLegId());
        record.setCampaignId(input.getCampaignId());
        record.setIsOutbound(input.getIsOutbound());
        record.setCallType(input.getCallType());
        record.setCurrentState(input.getEventType());
        record.setSystemDisposition(input.getSystemDisposition());
        record.setCallResult(input.getCallResult());
        record.setCallOriginateTime(input.getCallOriginateTime());

        // If this is a DISCONNECT event, apply the final metrics
        if ("DISCONNECTED".equals(input.getEventType())) {
            record.setCallEndTime(input.getCallEndTime());
            record.setHangupCauseDescription(input.getHangupCauseDescription());
            record.setHangupOnHold(input.getHangupOnHold());
            record.setIvrTime(input.getIvrTime());
            record.setRingingTime(input.getRingingTime());
            record.setTalkTime(input.getTalkTime());
        }

        callRepository.save(record);
        System.out.println("Live Update: Call " + input.getCallId() + " moved to state -> " + input.getEventType());
    }
}