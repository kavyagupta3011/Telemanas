package com.telemanas.eventconsumer.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Format saved to database 
@Entity
@Table(name = "agent_activity")
public class AgentActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)             
    private String sessionId;

    private Integer campaignId;

    private Instant readyStartTime;
    private Instant readyEndTime;

    private Instant breakEndTime;

    private String breakReason;
    private String agentBreakReason;
    
    // Getters and Setters
    public Long getId() { return id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Integer getCampaignId() { return campaignId; }
    public void setCampaignId(Integer campaignId) { this.campaignId = campaignId; }

    public Instant getReadyStartTime() { return readyStartTime; }
    public void setReadyStartTime(Instant readyStartTime) { this.readyStartTime = readyStartTime; }

    public Instant getReadyEndTime() { return readyEndTime; }
    public void setReadyEndTime(Instant readyEndTime) { this.readyEndTime = readyEndTime; }

    public Instant getBreakEndTime() { return breakEndTime; }
    public void setBreakEndTime(Instant breakEndTime) { this.breakEndTime = breakEndTime; }

    public String getBreakReason() { return breakReason; }
    public void setBreakReason(String breakReason) { this.breakReason = breakReason; }

    public String getAgentBreakReason() { return agentBreakReason; }
    public void setAgentBreakReason(String agentBreakReason) { this.agentBreakReason = agentBreakReason; }
}
