package com.telemanas.eventconsumer.model;

import java.time.Instant;

public class AutoCallInput {

    private String Id;
    private String sessionId;
    private Instant autoCallOnStartTime;
    private Instant autoCallOnEndTime;
    private Instant autoCallOffEndTime;
    private String autoCallStartReason;
    private String endReason;
    private Integer campaignId;

    
    public String getId() { return Id; }
    public void setId(String Id) { this.Id = Id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Instant getAutoCallOnStartTime() { return autoCallOnStartTime; }
    public void setAutoCallOnStartTime(Instant autoCallOnStartTime) { this.autoCallOnStartTime = autoCallOnStartTime; }

    public Instant getAutoCallOnEndTime() { return autoCallOnEndTime; }
    public void setAutoCallOnEndTime(Instant autoCallOnEndTime) { this.autoCallOnEndTime = autoCallOnEndTime; }

    public Instant getAutoCallOffEndTime() { return autoCallOffEndTime; }
    public void setAutoCallOffEndTime(Instant autoCallOffEndTime) { this.autoCallOffEndTime = autoCallOffEndTime; }

    public String getAutoCallStartReason() { return autoCallStartReason; }
    public void setAutoCallStartReason(String autoCallStartReason) { this.autoCallStartReason = autoCallStartReason; }

    public String getEndReason() { return endReason; }
    public void setEndReason(String endReason) { this.endReason = endReason; }

    public Integer getCampaignId() { return campaignId; }
    public void setCampaignId(Integer campaignId) { this.campaignId = campaignId; }
}
