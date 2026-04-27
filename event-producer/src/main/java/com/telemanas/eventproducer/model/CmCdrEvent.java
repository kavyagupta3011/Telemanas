package com.telemanas.eventproducer.model;

import java.time.Instant;

// Represents a CM CDR event in the system, containing detailed information about the call and its termination.
public class CmCdrEvent {

    private String callLegId;
    private String hangupCause;
    private Integer hangupCauseCode;
    private Long setupTime;
    private Long ringTime;
    private Long talkTime;
    private Instant startTime;
    private Instant endTime;
    private Instant voiceResourceInitializationTime;
    private String whichSideHungup;
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