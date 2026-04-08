package com.telemanas.eventconsumer.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CallRecordInput {

    private String eventType;
    private Instant eventTimestamp;

    @JsonProperty("crt_object_id")
    private String crtObjectId;
    
    @JsonProperty("call_id")
    private String callId;
    
    @JsonProperty("call_leg_id")
    private String callLegId;
    
    @JsonProperty("campaign_id")
    private Integer campaignId;

    @JsonProperty("is_outbound")
    private Boolean isOutbound;
    
    @JsonProperty("call_type")
    private String callType;

    @JsonProperty("system_disposition")
    private String systemDisposition;

    @JsonProperty("date_added")
    private Integer dateAdded;
    
    @JsonProperty("call_result")
    private String callResult;

    @JsonProperty("hangup_cause")
    private String hangupCause;
    
    @JsonProperty("hangup_cause_description")
    private String hangupCauseDescription;
    
    @JsonProperty("hangup_on_hold")
    private Boolean hangupOnHold;

    @JsonProperty("call_originate_time")
    private Instant callOriginateTime;
    
    @JsonProperty("call_end_time")
    private Instant callEndTime;

    @JsonProperty("setup_time")
    private Long setupTime;
    
    @JsonProperty("ringing_time")
    private Long ringingTime;

    @JsonProperty("ivr_time")
    private Long ivrTime;
    
    @JsonProperty("talk_time")
    private Long talkTime;

    @JsonProperty("hold_time")
    private Long holdTime;

    // --- GETTERS AND SETTERS ---
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public Instant getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(Instant eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    public String getCrtObjectId() { return crtObjectId; }
    public void setCrtObjectId(String crtObjectId) { this.crtObjectId = crtObjectId; }

    public String getCallId() { return callId; }
    public void setCallId(String callId) { this.callId = callId; }

    public String getCallLegId() { return callLegId; }
    public void setCallLegId(String callLegId) { this.callLegId = callLegId; }

    public Integer getCampaignId() { return campaignId; }
    public void setCampaignId(Integer campaignId) { this.campaignId = campaignId; }

    public Boolean getIsOutbound() { return isOutbound; }
    public void setIsOutbound(Boolean outbound) { isOutbound = outbound; }

    public String getCallType() { return callType; }
    public void setCallType(String callType) { this.callType = callType; }

    public String getSystemDisposition() { return systemDisposition; }
    public void setSystemDisposition(String systemDisposition) { this.systemDisposition = systemDisposition; }

    public String getCallResult() { return callResult; }
    public void setCallResult(String callResult) { this.callResult = callResult; }

    public Instant getCallOriginateTime() { return callOriginateTime; }
    public void setCallOriginateTime(Instant callOriginateTime) { this.callOriginateTime = callOriginateTime; }

    public Instant getCallEndTime() { return callEndTime; }
    public void setCallEndTime(Instant callEndTime) { this.callEndTime = callEndTime; }

    public String getHangupCauseDescription() { return hangupCauseDescription; }
    public void setHangupCauseDescription(String hangupCauseDescription) { this.hangupCauseDescription = hangupCauseDescription; }

    public Boolean getHangupOnHold() { return hangupOnHold; }
    public void setHangupOnHold(Boolean hangupOnHold) { this.hangupOnHold = hangupOnHold; }

    public Long getIvrTime() { return ivrTime; }
    public void setIvrTime(Long ivrTime) { this.ivrTime = ivrTime; }

    public Long getRingingTime() { return ringingTime; }
    public void setRingingTime(Long ringingTime) { this.ringingTime = ringingTime; }

    public Long getTalkTime() { return talkTime; }
    public void setTalkTime(Long talkTime) { this.talkTime = talkTime; }

    public String getHangupCause() { return hangupCause; }
    public void setHangupCause(String hangupCause) { this.hangupCause = hangupCause; }

    public Integer getDateAdded() { return dateAdded; }
    public void setDateAdded(Integer dateAdded) { this.dateAdded = dateAdded; }

    public Long getSetupTime() { return setupTime; }
    public void setSetupTime(Long setupTime) { this.setupTime = setupTime; }

    public Long getHoldTime() { return holdTime; }
    public void setHoldTime(Long holdTime) { this.holdTime = holdTime; }

}