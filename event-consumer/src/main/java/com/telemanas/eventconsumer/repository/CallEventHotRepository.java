package com.telemanas.eventconsumer.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.HotCallEvent;

public interface CallEventHotRepository
        extends JpaRepository<HotCallEvent, Long> {

    void deleteByTimestampBefore(Instant cutoff);
}
