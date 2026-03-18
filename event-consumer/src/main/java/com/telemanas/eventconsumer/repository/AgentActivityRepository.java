package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.AgentActivity;

public interface AgentActivityRepository extends JpaRepository<AgentActivity, Long> {
}
