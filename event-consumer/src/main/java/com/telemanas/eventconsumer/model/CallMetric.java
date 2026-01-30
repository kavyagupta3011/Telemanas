package com.telemanas.eventconsumer.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "call_metrics")
public class CallMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String state;
    private int totalCalls;
    private int emergencyCalls;
    private Instant lastUpdated;

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public int getTotalCalls() {
        return totalCalls;
    }

    public int getEmergencyCalls() {
        return emergencyCalls;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    // ===== SETTERS =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTotalCalls(int totalCalls) {
        this.totalCalls = totalCalls;
    }

    public void setEmergencyCalls(int emergencyCalls) {
        this.emergencyCalls = emergencyCalls;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
