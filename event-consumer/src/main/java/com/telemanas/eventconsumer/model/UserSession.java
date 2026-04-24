package com.telemanas.eventconsumer.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Format saved to database
@Entity
@Table(name = "user_sessions")
public class UserSession{

    @Id
    private String sessionId;
    private String userId;
    private String reason;     
    private Instant loginTime;
    private Instant logoutTime;

    // getters and setters
    public Instant getLoginTime() { return loginTime; }
    public void setLoginTime(Instant loginTime) { this.loginTime = loginTime; }

    public Instant getLogoutTime() { return logoutTime; }
    public void setLogoutTime(Instant logoutTime) { this.logoutTime = logoutTime; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
