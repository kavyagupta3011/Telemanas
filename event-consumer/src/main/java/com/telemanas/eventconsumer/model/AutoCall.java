package com.telemanas.eventconsumer.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Format saved to database
@Entity
@Table(name = "autocall_activity")
public class AutoCall {

    @Id
    private String id;
    private String sessionId;
    private Instant autoCallOnStartTime;
    private Instant autoCallOnEndTime;
    private Instant autoCallOffEndTime;
    private String autoCallStartReason;
    private String endReason;
    private Integer campaignId;

    // getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getAutoCallOnStartTime() {
        return autoCallOnStartTime;
    }

    public void setAutoCallOnStartTime(Instant autoCallOnStartTime) {
        this.autoCallOnStartTime = autoCallOnStartTime;
    }

    public Instant getAutoCallOnEndTime() {
        return autoCallOnEndTime;
    }

    public void setAutoCallOnEndTime(Instant autoCallOnEndTime) {
        this.autoCallOnEndTime = autoCallOnEndTime;
    }

    public Instant getAutoCallOffEndTime() {
        return autoCallOffEndTime;
    }

    public void setAutoCallOffEndTime(Instant autoCallOffEndTime) {
        this.autoCallOffEndTime = autoCallOffEndTime;
    }

    public String getAutoCallStartReason() {
        return autoCallStartReason;
    }

    public void setAutoCallStartReason(String autoCallStartReason) {
        this.autoCallStartReason = autoCallStartReason;
    }

    public String getEndReason() {
        return endReason;
    }

    public void setEndReason(String endReason) {
        this.endReason = endReason;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }
}