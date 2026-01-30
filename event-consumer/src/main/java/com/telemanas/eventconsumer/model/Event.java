package com.telemanas.eventconsumer.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "call_events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String state;
    private String city;
    private String gender;
    private String ageGroup;
    private String eventType;
    private String userId;
    private boolean isEmergency;  

    private Instant timestamp;

    // getters & setterss
    // ===== GETTERS =====

    public String getEventType() {
        return eventType;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public String getGender() {
        return gender;
    }

    // ===== SETTERS =====

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
