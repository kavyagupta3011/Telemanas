package com.telemanas.eventconsumer.model;

import java.time.Instant;

public class UserSession{

    private String sessionId;
    private String userId;
     private String reason;      // only for LOGOUT
      private Instant loginTime;
    private Instant logoutTime;

    
    public Instant getLoginTime() { return loginTime; }
    public void setLoginTime(Instant loginTime) { this.loginTime = loginTime; }
    public Instant getLogoutTime() { return logoutTime; }
    public void setLogoutTime(Instant logoutTime) { this.logoutTime = logoutTime; }

    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public String getReason() { return reason; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setReason(String reason) { this.reason = reason; }
}
