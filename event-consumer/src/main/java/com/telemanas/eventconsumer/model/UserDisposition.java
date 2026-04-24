package com.telemanas.eventconsumer.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Format saved to database
@Entity
@Table(name = "user_disposition_history")
public class UserDisposition {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "call_id")
    private String callId;

    @Column(name = "call_leg_id")
    private String callLegId;

    @Column(name = "date_added")
    private Instant dateAdded;

    @Column(name = "user_disposition_time")
    private Instant userDispositionTime;

    @Column(name = "transfer_time")
    private Instant transferTime;

    @Column(name = "transfer_to")
    private String transferTo;

    @Column(name = "disposition_class")
    private String dispositionClass;

    @Column(name = "disposition_code")
    private String dispositionCode;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "wrap_time")
    private Long wrapTime;

    @Column(name = "talk_time")
    private Long talkTime;

    @Column(name = "working")
    private Boolean working;

    @Column(name = "disposed_by_crm")
    private Boolean disposedByCrm;

    @Column(name = "auto_call_on_time")
    private Instant autoCallOnTime;

    @Column(name = "auto_call_off_time")
    private Instant autoCallOffTime;

    @Column(name = "user_connected_time")
    private Instant userConnectedTime;

    @Column(name = "user_disconnected_time")
    private Instant userDisconnectedTime;

    @Column(name = "campaign_id")
    private Integer campaignId;

    @Column(name = "association_type")
    private String associationType;

    @Column(name = "customer_hold_time")
    private Long customerHoldTime;

    @Column(name = "hold_time")
    private Long holdTime;

    // getters and setters
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