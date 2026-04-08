package com.telemanas.eventproducer.model;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDisposition {

    private String eventType;
    private Instant eventTimestamp;

    @JsonProperty("id")
    private String id;

    @JsonProperty("call_id")
    private String callId;

    @JsonProperty("call_leg_id")
    private String callLegId;

    @JsonProperty("date_added")
    private Instant dateAdded;

    @JsonProperty("user_disposition_time")
    private Instant userDispositionTime;

    @JsonProperty("transfer_time")
    private Instant transferTime;

    @JsonProperty("transfer_to")
    private String transferTo;

    @JsonProperty("disposition_class")
    private String dispositionClass;

    @JsonProperty("disposition_code")
    private String dispositionCode;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("wrap_time")
    private Long wrapTime;

    @JsonProperty("talk_time")
    private Long talkTime;

    @JsonProperty("working")
    private Boolean working;

    @JsonProperty("disposed_by_crm")
    private Boolean disposedByCrm;

    @JsonProperty("auto_call_on_time")
    private Instant autoCallOnTime;

    @JsonProperty("auto_call_off_time")
    private Instant autoCallOffTime;

    @JsonProperty("user_connected_time")
    private Instant userConnectedTime;

    @JsonProperty("user_disconnected_time")
    private Instant userDisconnectedTime;

    @JsonProperty("campaign_id")
    private Integer campaignId;

    @JsonProperty("association_type")
    private String associationType;

    @JsonProperty("customer_hold_time")
    private Long customerHoldTime;

    @JsonProperty("hold_time")
    private Long holdTime;

    // --- GETTERS AND SETTERS ---

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