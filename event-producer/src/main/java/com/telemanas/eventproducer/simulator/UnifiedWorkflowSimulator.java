package com.telemanas.eventproducer.simulator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.telemanas.eventproducer.model.AgentActivityEvent;
import com.telemanas.eventproducer.model.AutoCallEvent;
import com.telemanas.eventproducer.model.UserSessionEvent;
import com.telemanas.eventproducer.service.AgentActivityProducerService;
import com.telemanas.eventproducer.service.AutoCallProducerService;
import com.telemanas.eventproducer.service.UserSessionProducerService;

@Component
public class UnifiedWorkflowSimulator implements CommandLineRunner {

    private final UserSessionProducerService sessionProducer;
    private final AgentActivityProducerService activityProducer;
    private final AutoCallProducerService autoCallProducer;
    private final Random random = new Random();

    public UnifiedWorkflowSimulator(
            UserSessionProducerService sessionProducer,
            AgentActivityProducerService activityProducer,
            AutoCallProducerService autoCallProducer) {
        this.sessionProducer = sessionProducer;
        this.activityProducer = activityProducer;
        this.autoCallProducer = autoCallProducer;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting Unified Tele-MANAS Workflow Simulation...");

        List<String> users = List.of("user1", "user2", "user3", "user4", "user5");

        // We will simulate the last 24 hours of data
        Instant baseTime = Instant.now().minus(24, ChronoUnit.HOURS);

        for (String user : users) {
            // Each user does 3 full shifts/sessions
            for (int i = 0; i < 3; i++) {

                // 1. Generate Shared IDs
                String sessionId = "sess-" + UUID.randomUUID().toString().substring(0, 8);
                Integer campaignId = 101 + random.nextInt(3);

                // 2. Build Chronological Timelines
                Instant loginTime = baseTime.plus(random.nextInt(60), ChronoUnit.MINUTES);

                // Agent gets ready 2 minutes after login
                Instant readyStartTime = loginTime.plusSeconds(120);

                // AutoCall starts 1 minute after getting ready
                Instant autoCallStart = readyStartTime.plusSeconds(60);

                // AutoCall runs for 45 to 90 minutes
                Instant autoCallEnd = autoCallStart.plus(45 + random.nextInt(45), ChronoUnit.MINUTES);

                // Agent stops being ready 5 minutes after AutoCall ends
                Instant readyEndTime = autoCallEnd.plusSeconds(300);

                // User logs out 2 minutes after stopping ready status
                Instant logoutTime = readyEndTime.plusSeconds(120);

                // Push base time forward so the next session doesn't overlap
                baseTime = logoutTime.plus(2, ChronoUnit.HOURS);

                // ==========================================
                // 3. Emit Events Slowly to Kafka
                // ==========================================

                // A. Send LOGIN
                UserSessionEvent loginEvent = new UserSessionEvent();
                loginEvent.setSessionId(sessionId);
                loginEvent.setUserId(user);
                loginEvent.setEventType("LOGIN");
                loginEvent.setTimestamp(loginTime);
                sessionProducer.send(loginEvent);
                System.out.println("→ " + user + " LOGGED IN (Session: " + sessionId + ")");
                Thread.sleep(1000); // Wait 1 second

                // B. Send AutoCall (Complete record)
                AutoCallEvent autoCall = new AutoCallEvent();
                autoCall.setId("ac-" + UUID.randomUUID().toString().substring(0, 8));
                autoCall.setSessionId(sessionId);
                autoCall.setCampaignId(campaignId);
                autoCall.setAutoCallOnStartTime(autoCallStart);
                autoCall.setAutoCallOnEndTime(autoCallEnd);
                autoCall.setAutoCallOffEndTime(autoCallEnd.plusSeconds(10));
                autoCall.setAutoCallStartReason("System Trigger");
                autoCall.setEndReason("Batch Completed");
                autoCallProducer.send(autoCall);
                System.out.println("  ↳ AutoCall completed for " + user);
                Thread.sleep(1000); // Wait 1 second

                // C. Send Agent Activity (Complete record)
                AgentActivityEvent activity = new AgentActivityEvent();
                activity.setSessionId(sessionId);
                activity.setCampaignId(campaignId);
                activity.setReadyStartTime(readyStartTime);
                activity.setReadyEndTime(readyEndTime);
                activity.setEventType("AGENT_ACTIVITY_COMPLETE");
                activityProducer.send(activity);
                System.out.println("  ↳ Agent Activity logged for " + user);
                Thread.sleep(1000); // Wait 1 second

                // D. Send LOGOUT
                UserSessionEvent logoutEvent = new UserSessionEvent();
                logoutEvent.setSessionId(sessionId);
                logoutEvent.setUserId(user);
                logoutEvent.setEventType("LOGOUT");
                logoutEvent.setTimestamp(logoutTime);
                logoutEvent.setReason("End of Shift");
                sessionProducer.send(logoutEvent);
                System.out.println("← " + user + " LOGGED OUT\n");
                Thread.sleep(2000); // Wait 2 seconds before next session
            }
        }
        System.out.println("Unified workflow simulation complete!");
    }
}