package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.AgentActivity;
import com.telemanas.eventconsumer.model.AgentActivityInput;
import com.telemanas.eventconsumer.repository.AgentActivityRepository;
import java.util.UUID;

// Service responsible for consuming agent activity events from Kafka and processing them ( saving to the database).
@Service
public class AgentActivityConsumer {

    private final AgentActivityRepository activityRepository;

    public AgentActivityConsumer(AgentActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // Kafka listener to topic "agent-activity-events" with group ID "telemanas-activity-group". It uses a specific container factory for deserialization.
    @KafkaListener(
        topics = "agent-activity-events", 
        groupId = "telemanas-activity-group",
        containerFactory = "agentActivityKafkaListenerContainerFactory"
    )

  public void consumeActivityEvent(AgentActivityInput input) {

    AgentActivity activity = activityRepository
        .findBySessionId(input.getSessionId())
        .orElseGet(AgentActivity::new);

    activity.setId(UUID.randomUUID().toString());
    activity.setSessionId(input.getSessionId());
    activity.setCampaignId(input.getCampaignId());

    if (input.getReadyStartTime() != null) {
        activity.setReadyStartTime(input.getReadyStartTime());
    }

    if (input.getReadyEndTime() != null) {
        activity.setReadyEndTime(input.getReadyEndTime());
    }

    if (input.getBreakEndTime() != null) {
        activity.setBreakEndTime(input.getBreakEndTime());
    }

    if (input.getBreakReason() != null) {
        activity.setBreakReason(input.getBreakReason());
    }

    if (input.getAgentBreakReason() != null) {
        activity.setAgentBreakReason(input.getAgentBreakReason());
    }

    activityRepository.save(activity);

    System.out.println("Processed " + input.getEventType() +
            " event for session: " + input.getSessionId());
}
}