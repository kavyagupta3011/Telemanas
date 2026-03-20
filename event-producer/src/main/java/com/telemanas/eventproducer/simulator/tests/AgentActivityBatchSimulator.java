package com.telemanas.eventproducer.simulator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;

import com.telemanas.eventproducer.model.AgentActivityEvent;
import com.telemanas.eventproducer.service.AgentActivityProducerService;

//@Component
public class AgentActivityBatchSimulator implements CommandLineRunner {

    private final AgentActivityProducerService producer;
    private final Random random = new Random();

    public AgentActivityBatchSimulator(AgentActivityProducerService producer) {
        this.producer = producer;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Starting slow generation of agent activity data...");

        List<String> users = List.of("user1", "user2", "user3", "user4", "user5");
        LocalDate randomDate = LocalDate.now().minusDays(random.nextInt(2));

        for (String user : users) {
            int activityCount = 3 + random.nextInt(3);

            for (int i = 0; i < activityCount; i++) {
                LocalTime startTime = randomTime();
                LocalDateTime startDateTime = LocalDateTime.of(randomDate, startTime);
                LocalDateTime readyEndDateTime = startDateTime.plusMinutes(30 + random.nextInt(90));
                LocalDateTime breakEndDateTime = readyEndDateTime.plusMinutes(5 + random.nextInt(10));

                AgentActivityEvent event = new AgentActivityEvent();
                event.setSessionId(UUID.randomUUID().toString());
                event.setCampaignId(100 + random.nextInt(5)); 
                event.setReadyStartTime(startDateTime.toInstant(ZoneOffset.UTC));
                event.setReadyEndTime(readyEndDateTime.toInstant(ZoneOffset.UTC));
                event.setBreakEndTime(breakEndDateTime.toInstant(ZoneOffset.UTC));
                event.setBreakReason(randomBreakReason());
                event.setAgentBreakReason("Agent requested break");
                event.setEventType("AGENT_ACTIVITY_COMPLETE");
                event.setCreatedAt(startDateTime.toInstant(ZoneOffset.UTC));

                producer.send(event);
                System.out.println("Sent agent activity for " + user);

                // SLOW DOWN: Pause between 1 and 3 seconds
                Thread.sleep(1000 + random.nextInt(2000));
            }
        }
        System.out.println("Agent activity data generation complete.");
    }

    private LocalTime randomTime() {
        return LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));
    }

    private String randomBreakReason() {
        String[] reasons = {"Tea Break", "Lunch", "Bio Break", "Training"};
        return reasons[random.nextInt(reasons.length)];
    }
}