package com.telemanas.eventconsumer.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Format saved to database
@Entity
@Table(name = "cm_cdr_history")
public class CmCdr {

    @Id
    @Column(name = "call_leg_id")
    private String callLegId;

    @Column(name = "hangup_cause")
    private String hangupCause;

    @Column(name = "hangup_cause_code")
    private Integer hangupCauseCode;

    @Column(name = "setup_time")
    private Long setupTime;

    @Column(name = "ring_time")
    private Long ringTime;

    @Column(name = "talk_time")
    private Long talkTime;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "voice_resource_initialization_time")
    private Instant voiceResourceInitializationTime;

    @Column(name = "which_side_hungup")
    private String whichSideHungup;

    @Column(name = "internal_hangup_reason")
    private String internalHangupReason;

    // getters and setters
    public String getCallLegId() { return callLegId; }
    public void setCallLegId(String callLegId) { this.callLegId = callLegId; }

    public String getHangupCause() { return hangupCause; }
    public void setHangupCause(String hangupCause) { this.hangupCause = hangupCause; }

    public Integer getHangupCauseCode() { return hangupCauseCode; }
    public void setHangupCauseCode(Integer hangupCauseCode) { this.hangupCauseCode = hangupCauseCode; }

    public Long getSetupTime() { return setupTime; }
    public void setSetupTime(Long setupTime) { this.setupTime = setupTime; }

    public Long getRingTime() { return ringTime; }
    public void setRingTime(Long ringTime) { this.ringTime = ringTime; }

    public Long getTalkTime() { return talkTime; }
    public void setTalkTime(Long talkTime) { this.talkTime = talkTime; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public Instant getVoiceResourceInitializationTime() { return voiceResourceInitializationTime; }
    public void setVoiceResourceInitializationTime(Instant voiceResourceInitializationTime) { 
        this.voiceResourceInitializationTime = voiceResourceInitializationTime; 
    }

    public String getWhichSideHungup() { return whichSideHungup; }
    public void setWhichSideHungup(String whichSideHungup) { this.whichSideHungup = whichSideHungup; }

    public String getInternalHangupReason() { return internalHangupReason; }
    public void setInternalHangupReason(String internalHangupReason) { 
        this.internalHangupReason = internalHangupReason; 
    }

}