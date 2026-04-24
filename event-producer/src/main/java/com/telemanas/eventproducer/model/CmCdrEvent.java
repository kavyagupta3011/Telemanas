package com.telemanas.eventproducer.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

// Represents a CM CDR event in the system, containing detailed information about the call and its termination.
public class CmCdrEvent {

    @JsonProperty("call_leg_id")
    private String callLegId;

    @JsonProperty("hangup_cause")
    private String hangupCause;

    @JsonProperty("hangup_cause_code")
    private Integer hangupCauseCode;

    @JsonProperty("setup_time")
    private Long setupTime;

    @JsonProperty("ring_time")
    private Long ringTime;

    @JsonProperty("talk_time")
    private Long talkTime;

    @JsonProperty("start_time")
    private Instant startTime;

    @JsonProperty("end_time")
    private Instant endTime;

    @JsonProperty("voice_resource_initialization_time")
    private Instant voiceResourceInitializationTime;

    @JsonProperty("which_side_hungup")
    private String whichSideHungup;

    @JsonProperty("internal_hangup_reason")
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