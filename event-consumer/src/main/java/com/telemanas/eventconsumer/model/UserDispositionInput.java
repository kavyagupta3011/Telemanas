package com.telemanas.eventconsumer.model;
import java.time.Instant;

// Format received from Kafka
public class UserDispositionInput {
    private String eventType;
    private Instant eventTimestamp;

    private String id;
    private String callId;
    private String callLegId;
    private Instant dateAdded;
    private Instant userDispositionTime;
    private Instant transferTime;
    private String transferTo;
    private String dispositionClass;
    private String dispositionCode;
    private String userId;
    private String sessionId;
    private Long wrapTime;
    private Long talkTime;
    private Boolean working;
    private Boolean disposedByCrm;
    private Instant autoCallOnTime;
    private Instant autoCallOffTime;
    private Instant userConnectedTime;
    private Instant userDisconnectedTime;
    private Integer campaignId;
    private String associationType;
    private Long customerHoldTime;
    private Long holdTime;

    // getters and setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Instant getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(Instant eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCallId() { return callId; }
    public void setCallId(String callId) { this.callId = callId; }

    public String getCallLegId() { return callLegId; }
    public void setCallLegId(String callLegId) { this.callLegId = callLegId; }

    public Instant getDateAdded() { return dateAdded; }
    public void setDateAdded(Instant dateAdded) { this.dateAdded = dateAdded; }

    public Instant getUserDispositionTime() { return userDispositionTime; }
    public void setUserDispositionTime(Instant userDispositionTime) { this.userDispositionTime = userDispositionTime; }

    public Instant getTransferTime() { return transferTime; }
    public void setTransferTime(Instant transferTime) { this.transferTime = transferTime; }

    public String getTransferTo() { return transferTo; }
    public void setTransferTo(String transferTo) { this.transferTo = transferTo; }

    public String getDispositionClass() { return dispositionClass; }
    public void setDispositionClass(String dispositionClass) { this.dispositionClass = dispositionClass; }

    public String getDispositionCode() { return dispositionCode; }
    public void setDispositionCode(String dispositionCode) { this.dispositionCode = dispositionCode; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getWrapTime() { return wrapTime; }
    public void setWrapTime(Long wrapTime) { this.wrapTime = wrapTime; }

    public Long getTalkTime() { return talkTime; }
    public void setTalkTime(Long talkTime) { this.talkTime = talkTime; }

    public Boolean getWorking() { return working; }
    public void setWorking(Boolean working) { this.working = working; }

    public Boolean getDisposedByCrm() { return disposedByCrm; }
    public void setDisposedByCrm(Boolean disposedByCrm) { this.disposedByCrm = disposedByCrm; }

    public Instant getAutoCallOnTime() { return autoCallOnTime; }
    public void setAutoCallOnTime(Instant autoCallOnTime) { this.autoCallOnTime = autoCallOnTime; }

    public Instant getAutoCallOffTime() { return autoCallOffTime; }
    public void setAutoCallOffTime(Instant autoCallOffTime) { this.autoCallOffTime = autoCallOffTime; }

    public Instant getUserConnectedTime() { return userConnectedTime; }
    public void setUserConnectedTime(Instant userConnectedTime) { this.userConnectedTime = userConnectedTime; }

    public Instant getUserDisconnectedTime() { return userDisconnectedTime; }
    public void setUserDisconnectedTime(Instant userDisconnectedTime) { this.userDisconnectedTime = userDisconnectedTime; }

    public Integer getCampaignId() { return campaignId; }
    public void setCampaignId(Integer campaignId) { this.campaignId = campaignId; }

    public String getAssociationType() { return associationType; }
    public void setAssociationType(String associationType) { this.associationType = associationType; }

    public Long getCustomerHoldTime() { return customerHoldTime; }
    public void setCustomerHoldTime(Long customerHoldTime) { this.customerHoldTime = customerHoldTime; }

    public Long getHoldTime() { return holdTime; }
    public void setHoldTime(Long holdTime) { this.holdTime = holdTime; }
}
