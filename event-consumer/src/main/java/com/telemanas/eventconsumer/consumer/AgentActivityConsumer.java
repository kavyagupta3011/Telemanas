package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.AgentActivity;
import com.telemanas.eventconsumer.model.AgentActivityInput;
import com.telemanas.eventconsumer.repository.AgentActivityRepository;

@Service
public class AgentActivityConsumer {

    private final AgentActivityRepository activityRepository;

    public AgentActivityConsumer(AgentActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @KafkaListener(
        topics = "agent-activity-events", 
        groupId = "telemanas-activity-group",
        containerFactory = "agentActivityKafkaListenerContainerFactory"
    )

    public void consumeActivityEvent(AgentActivityInput input) {

        AgentActivity activity;

        // If the input provides an ID, try to find the existing record to update it.
        // Otherwise, create a new record.
        if (input.getId() != null) {
            activity = activityRepository.findById(input.getId())
                    .orElseGet(AgentActivity::new);
        } else {
            activity = new AgentActivity();
        }

        // Map the fields from the input to the database entity
        // Note: Mapping input.sessionId to entity.campaignSessionId
        activity.setCampaignSessionId(input.getSessionId());
        activity.setCampaignId(input.getCampaignId());
        
        // Map timestamps and reasons
        activity.setReadyStartTime(input.getReadyStartTime());
        activity.setReadyEndTime(input.getReadyEndTime());
        activity.setBreakEndTime(input.getBreakEndTime());
        activity.setBreakReason(input.getBreakReason());
        activity.setAgentBreakReason(input.getAgentBreakReason());

        // Save or update the record in the database
        activityRepository.save(activity);

        System.out.println("Processed " + input.getEventType() + " event for campaign session: " + input.getSessionId());
    }
}