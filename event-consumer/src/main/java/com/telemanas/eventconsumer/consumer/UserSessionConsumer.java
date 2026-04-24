package com.telemanas.eventconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.UserSession;
import com.telemanas.eventconsumer.model.UserSessionInput;
import com.telemanas.eventconsumer.repository.UserSessionRepository;

// Service responsible for consuming user session events from Kafka and processing them (saving/updating user session records in the database).
@Service
public class UserSessionConsumer {

    private final UserSessionRepository sessionRepository;

    public UserSessionConsumer(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    // Kafka listener to topic "user-session-events" with group ID "telemanas-session-group". It uses a specific container factory for deserialization.
    @KafkaListener(
        topics = "user-session-events", 
        groupId = "telemanas-session-group",
        containerFactory = "userSessionKafkaListenerContainerFactory"
    )

    public void consumeSessionEvent(UserSessionInput input) {
        
        if (input.getSessionId() == null) {
            System.err.println("Dropped event: Missing session ID.");
            return;
        }

        // Fetch existing session (if logging out) or create a new one (if logging in)
        UserSession session = sessionRepository.findById(input.getSessionId())
                .orElseGet(() -> {
                    UserSession newSession = new UserSession();
                    newSession.setSessionId(input.getSessionId());
                    newSession.setUserId(input.getUserId());
                    return newSession;
                });

        // Mapping 
        if ("LOGIN".equalsIgnoreCase(input.getEventType())) {
            session.setLoginTime(input.getTimestamp());
        } else if ("LOGOUT".equalsIgnoreCase(input.getEventType())) {
            session.setLogoutTime(input.getTimestamp());
            session.setReason(input.getReason());
        } else {
            System.err.println("Unknown event type: " + input.getEventType());
        }

        // Save or update the record in Postgres
        sessionRepository.save(session);
        System.out.println("Processed " + input.getEventType() + " for session: " + input.getSessionId());
    }
}