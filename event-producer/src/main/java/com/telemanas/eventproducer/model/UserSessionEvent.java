package com.telemanas.eventproducer.model;

import java.time.Instant;

// Represents a user session event (login/logout) in the system.
public class UserSessionEvent {
    private String sessionId;
    private String userId;
    private String reason;      // only for LOGOUT
    private String eventType;   // LOGIN or LOGOUT
    private Instant timestamp;
   
    // getters 
    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public String getEventType() { return eventType; }
    public Instant getTimestamp() { return timestamp; }
    public String getReason() { return reason; }
    // setters
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public void setReason(String reason) { this.reason = reason; }
}
