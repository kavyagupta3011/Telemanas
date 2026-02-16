package com.telemanas.eventconsumer.consumer;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.UserSession;
import com.telemanas.eventconsumer.model.UserSessionEvent;
import com.telemanas.eventconsumer.repository.UserSessionRepository;

@Service
public class KafkaUserSessionConsumer {

    private final UserSessionRepository repository;

    public KafkaUserSessionConsumer(UserSessionRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "user-session-events", groupId = "session-group")
    public void consume(UserSessionEvent event) {

        String sessionId = event.getSessionId();

        if ("LOGIN".equals(event.getEventType())) {

            // Create new session row
            UserSession session = new UserSession();
            session.setSessionId(sessionId);
            session.setUserId(event.getUserId());
            session.setLoginTime(event.getTimestamp());

            repository.save(session);

            System.out.println("Saved LOGIN for session: " + sessionId);
        }

        else if ("LOGOUT".equals(event.getEventType())) {

            // Fetch existing session
            Optional<UserSession> optional = repository.findById(sessionId);

            if (optional.isPresent()) {

                UserSession session = optional.get();
                session.setLogoutTime(event.getTimestamp());
                session.setReason(event.getReason());

                repository.save(session);

                System.out.println("Updated LOGOUT for session: " + sessionId);
            }
            else {
                System.out.println("Warning: Logout received without login for session: " + sessionId);
            }
        }
    }
}
