package com.telemanas.eventconsumer.consumer;

import java.time.Instant;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telemanas.eventconsumer.model.CallMetric;
import com.telemanas.eventconsumer.model.Event;
import com.telemanas.eventconsumer.repository.CallMetricRepository;
import com.telemanas.eventconsumer.repository.EventRepository;

@Service
public class KafkaEventConsumer {

    private final CallMetricRepository repository;
    private final EventRepository eventRepository;


   public KafkaEventConsumer(CallMetricRepository repository,
                          EventRepository eventRepository) {
    this.repository = repository;
    this.eventRepository = eventRepository;
}

@KafkaListener(topics = "call-events", groupId = "telemanas-metrics-group")
public void consume(Event event) {

    // Ensure timestamp exists
    if (event.getTimestamp() == null) {
        event.setTimestamp(Instant.now());
    }

    // Save raw event
    eventRepository.save(event);

    // Existing metrics logic
    String state = event.getState();
    boolean emergency = event.isEmergency();

    CallMetric metric = repository.findByState(state)
            .orElseGet(() -> {
                CallMetric m = new CallMetric();
                m.setState(state);
                m.setTotalCalls(0);
                m.setEmergencyCalls(0);
                return m;
            });

    metric.setTotalCalls(metric.getTotalCalls() + 1);
    if (emergency) {
        metric.setEmergencyCalls(metric.getEmergencyCalls() + 1);
    }

    metric.setLastUpdated(Instant.now());
    repository.save(metric);

    System.out.println("Saved event + updated metrics for " + state);
}
}
