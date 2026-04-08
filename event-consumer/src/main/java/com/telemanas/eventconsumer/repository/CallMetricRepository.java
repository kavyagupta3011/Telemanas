package com.telemanas.eventconsumer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.prev.CallMetric;

public interface CallMetricRepository extends JpaRepository<CallMetric, Long> {
    Optional<CallMetric> findByState(String state);
}
