package com.telemanas.eventconsumer.model;

import java.time.Instant;

// Format received from Kafka
public class UserSessionInput {

    private String sessionId;
    private String userId;
    private String reason;      
    private String eventType;   
    private Instant timestamp;
   
    // getters and setters 
    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public String getEventType() { return eventType; }
    public Instant getTimestamp() { return timestamp; }
    public String getReason() { return reason; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public void setReason(String reason) { this.reason = reason; }
}
