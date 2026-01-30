package com.telemanas.eventconsumer.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "call_events_hot")
public class HotCallEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;
    private String userId;
    private Instant timestamp;
    private String state;
    private String city;
    private boolean emergency;
    private String ageGroup;
    private String gender;

    public static HotCallEvent from(Event e) {
        HotCallEvent h = new HotCallEvent();
        h.eventType = e.getEventType();
        h.userId = e.getUserId();
        h.timestamp = e.getTimestamp();
        h.state = e.getState();
        h.city = e.getCity();
        h.emergency = e.isEmergency();
        h.ageGroup = e.getAgeGroup();
        h.gender = e.getGender();
        return h;
    }
}
