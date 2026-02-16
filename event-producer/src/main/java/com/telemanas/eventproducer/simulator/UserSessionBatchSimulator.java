package com.telemanas.eventproducer.simulator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.telemanas.eventproducer.model.UserSessionEvent;
import com.telemanas.eventproducer.service.UserSessionProducerService;

@Component
public class UserSessionBatchSimulator implements CommandLineRunner {

    private final UserSessionProducerService producer;
    private final Random random = new Random();

    public UserSessionBatchSimulator(UserSessionProducerService producer) {
        this.producer = producer;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Generating session data...");

        // 15 users
        List<String> users = List.of(
                "user1", "user2", "user3", "user4", "user5",
                "user6", "user7", "user8", "user9", "user10",
                "user11", "user12", "user13", "user14", "user15"
        );

        // Pick one random date within last 7 days
        LocalDate randomDate = LocalDate.now().minusDays(random.nextInt(2));

        for (String user : users) {

            // 4–5 sessions per user
            int sessionCount = 4 + random.nextInt(2);

            for (int i = 0; i < sessionCount; i++) {

                // Generate random login time
                LocalTime loginTime = randomTime();
                LocalDateTime loginDateTime = LocalDateTime.of(randomDate, loginTime);

                // Generate logout time AFTER login
                LocalDateTime logoutDateTime = loginDateTime.plusMinutes(30 + random.nextInt(180));

                // Same session ID for login & logout
                String sessionId = UUID.randomUUID().toString();

                // Create LOGIN event
                UserSessionEvent loginEvent = new UserSessionEvent();
                loginEvent.setSessionId(sessionId);
                loginEvent.setUserId(user);
                loginEvent.setEventType("LOGIN");
                loginEvent.setTimestamp(loginDateTime.toInstant(ZoneOffset.UTC));

                producer.send(loginEvent);

                // Create LOGOUT event
                UserSessionEvent logoutEvent = new UserSessionEvent();
                logoutEvent.setSessionId(sessionId);
                logoutEvent.setUserId(user);
                logoutEvent.setEventType("LOGOUT");
                logoutEvent.setTimestamp(logoutDateTime.toInstant(ZoneOffset.UTC));
                logoutEvent.setReason(randomReason());

                producer.send(logoutEvent);

                System.out.println("Generated session for " + user);
            }
        }

        System.out.println("Session data generation complete.");
    }

    // Generates random time between 00:00 and 23:59
    private LocalTime randomTime() {
        return LocalTime.of(
                random.nextInt(24),
                random.nextInt(60),
                random.nextInt(60)
        );
    }

    // Random logout reason
    private String randomReason() {
        String[] reasons = {
                "Logout from UI",
                "Session Timeout",
                "urgent work"
        };
        return reasons[random.nextInt(reasons.length)];
    }
}
