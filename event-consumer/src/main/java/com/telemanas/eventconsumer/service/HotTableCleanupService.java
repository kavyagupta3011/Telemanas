package com.telemanas.eventconsumer.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.repository.CallEventHotRepository;

@Service
public class HotTableCleanupService {

    private final CallEventHotRepository repository;

    public HotTableCleanupService(CallEventHotRepository repository) {
        this.repository = repository;
    }

    // Runs every day at 12:00 AM
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteDayBeforeYesterday() {

        // Start of YESTERDAY (00:00)
        Instant cutoff = LocalDate.now()
                .minusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        repository.deleteByTimestampBefore(cutoff);

        System.out.println("Deleted call events before " + cutoff);
    }
}
