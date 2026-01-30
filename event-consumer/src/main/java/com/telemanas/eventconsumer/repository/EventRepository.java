package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
