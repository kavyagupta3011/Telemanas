package com.telemanas.eventconsumer.repository;

import com.telemanas.eventconsumer.model.CallMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CallMetricRepository extends JpaRepository<CallMetric, Long> {
    Optional<CallMetric> findByState(String state);
}
