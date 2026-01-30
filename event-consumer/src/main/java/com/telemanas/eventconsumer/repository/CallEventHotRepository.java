package com.telemanas.eventconsumer.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.Event;

public interface CallEventHotRepository
        extends JpaRepository<Event, Long> {

    void deleteByTimestampBefore(Instant cutoff);
}
