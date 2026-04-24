package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telemanas.eventconsumer.model.AgentActivity;

// Repository for persisting agent activity data to the database.
@Repository
public interface AgentActivityRepository extends JpaRepository<AgentActivity, Long> {
}
