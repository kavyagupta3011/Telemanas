package com.telemanas.eventproducer.simulator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.telemanas.eventproducer.model.AutoCallEvent;
import com.telemanas.eventproducer.service.AutoCallProducerService;

@Component
public class AutoCallBatchSimulator implements CommandLineRunner {

    private final AutoCallProducerService producer;
    private final Random random = new Random();

    public AutoCallBatchSimulator(AutoCallProducerService producer) {
        this.producer = producer;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Starting slow generation of AutoCall data...");

        LocalDate randomDate = LocalDate.now().minusDays(random.nextInt(2));

        for (int i = 0; i < 30; i++) {
            LocalTime startTime = randomTime();
            LocalDateTime startDateTime = LocalDateTime.of(randomDate, startTime);
            LocalDateTime endDateTime = startDateTime.plusMinutes(60 + random.nextInt(180));

            AutoCallEvent event = new AutoCallEvent();
            event.setId(UUID.randomUUID().toString());
            event.setSessionId(UUID.randomUUID().toString());
            event.setCampaignId(100 + random.nextInt(5));
            event.setAutoCallOnStartTime(startDateTime.toInstant(ZoneOffset.UTC));
            event.setAutoCallOnEndTime(endDateTime.toInstant(ZoneOffset.UTC));
            event.setAutoCallOffEndTime(endDateTime.plusMinutes(5).toInstant(ZoneOffset.UTC));
            event.setAutoCallStartReason("System trigger");
            event.setEndReason(randomEndReason());

            producer.send(event);
            System.out.println("Sent AutoCall event: " + event.getId());

            // SLOW DOWN: Pause between 1 and 2 seconds
            Thread.sleep(1000 + random.nextInt(1000));
        }

        System.out.println("AutoCall data generation complete.");
    }

    private LocalTime randomTime() {
        return LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));
    }

    private String randomEndReason() {
        String[] reasons = {"Campaign Completed", "Manual Stop", "Error Limit Reached"};
        return reasons[random.nextInt(reasons.length)];
    }
}