#!/usr/bin/env bash
set -euo pipefail

BASE_URL="http://3.109.71.75:8082/producer"

iso_time() {
  local minutes_ago="$1"
  date -u -d "${minutes_ago} minutes ago" +"%Y-%m-%dT%H:%M:%SZ"
}

rand_between() {
  local min="$1"
  local max="$2"
  echo $((RANDOM % (max - min + 1) + min))
}

pick() {
  local -n arr="$1"
  local idx=$((RANDOM % ${#arr[@]}))
  echo "${arr[$idx]}"
}

CAMPAIGNS=(13 14 42 24 25 26 10 22 11 28 20 32 35 33 23 15 19 37 17 18 12 43 44 40 38 3 39 21 27 30 41 36 29 31 34 16 47)
CALL_TYPES=("inbound.call.dial" "outbound.manual.dial" "transferred.to.campaign.dial")
SYSTEM_DISPOSITIONS=("CONNECTED" "CALL_HANGUP" "PROVIDER_TEMP_FAILURE" "NO_ANSWER")
HANGUP_CAUSES=("normal.clearing" "provider.temp.failure" "user.busy" "no.answer")
HANGUP_DESCRIPTIONS=("Normal Clearing" "Request Terminated" "Temporarily Unavailable" "User Busy")
BREAK_REASONS=("Lunch" "Tea" "Snacks" "Meeting" "Unavailable" "System")
AGENT_BREAK_REASONS=("Just.Logged.In" "erroneous.channel.system.initiated.break" "NORMAL" "Timeout")
DISPOSITION_CLASSES=("NoCallback" "Callback" "System Defined" "application.disposed")
DISPOSITION_CODES=("No_Callback" "CB" "wrap.timeout" "application.disposed")
ASSOCIATION_TYPES=("transfer.to.campaign.association" "manualdial.association" "confer.association" "transfer.association")

post_json() {
  local endpoint="$1"
  local payload="$2"

  local status
  status=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "${BASE_URL}${endpoint}" \
    -H "Content-Type: application/json" \
    -d "${payload}")

  echo "POST ${endpoint} -> ${status}"
}

# Send 25 additional records per endpoint, using IDs 101-125
for i in $(seq 101 125); do
  auto_start=$(iso_time $((i * 2 + 30)))
  auto_end=$(iso_time $((i * 2 + 10)))
  auto_off=$(iso_time $((i * 2 + 5)))
  campaign_id=$(pick CAMPAIGNS)
  auto_reason=$(pick BREAK_REASONS)
  end_reason=$(pick BREAK_REASONS)

  post_json "/api/auto-call" "{\"id\":\"ac-${i}\",\"sessionId\":\"s-${i}\",\"autoCallOnStartTime\":\"${auto_start}\",\"autoCallOnEndTime\":\"${auto_end}\",\"autoCallOffEndTime\":\"${auto_off}\",\"autoCallStartReason\":\"${auto_reason}\",\"endReason\":\"${end_reason}\",\"campaignId\":${campaign_id}}"
done

for i in $(seq 101 125); do
  ready_start=$(iso_time $((i * 2 + 40)))
  ready_end="null"
  break_end="null"
  break_reason="null"
  agent_break_reason="null"
  event_type="AGENT_SET_READY"
  campaign_id=$(pick CAMPAIGNS)

  if (( i % 2 == 0 )); then
    ready_end="\"$(iso_time $((i * 2 + 20)))\""
    break_end="\"$(iso_time $((i * 2 + 10)))\""
    break_reason="\"$(pick BREAK_REASONS)\""
    agent_break_reason="\"$(pick AGENT_BREAK_REASONS)\""
    event_type="AGENT_SET_NOT_READY"
  fi

  post_json "/api/agent-activity" "{\"sessionId\":\"s-${i}\",\"campaignId\":${campaign_id},\"readyStartTime\":\"${ready_start}\",\"readyEndTime\":${ready_end},\"breakEndTime\":${break_end},\"breakReason\":${break_reason},\"agentBreakReason\":${agent_break_reason},\"eventType\":\"${event_type}\"}"
done

for i in $(seq 101 125); do
  login_time=$(iso_time $((i * 4 + 80)))
  post_json "/api/user-sessions" "{\"sessionId\":\"s-${i}\",\"userId\":\"u-${i}\",\"eventType\":\"LOGIN\",\"timestamp\":\"${login_time}\",\"reason\":null}"
done

for i in $(seq 101 125); do
  logout_time=$(iso_time $((i * 4 + 30)))
  post_json "/api/user-sessions" "{\"sessionId\":\"s-${i}\",\"userId\":\"u-${i}\",\"eventType\":\"LOGOUT\",\"timestamp\":\"${logout_time}\",\"reason\":\"Logout from UI\"}"
done

for i in $(seq 101 125); do
  event_ts=$(iso_time $((i * 2 + 25)))
  orig_time=$(iso_time $((i * 2 + 27)))
  end_time="\"$(iso_time $((i * 2 + 5)))\""
  event_type=$(pick SYSTEM_DISPOSITIONS)
  call_type=$(pick CALL_TYPES)
  system_disposition=$(pick SYSTEM_DISPOSITIONS)
  call_result=$([ $((i % 3)) -eq 0 ] && echo "FAILURE" || echo "SUCCESS")
  hangup_cause=$(pick HANGUP_CAUSES)
  hangup_desc=$(pick HANGUP_DESCRIPTIONS)
  hold_flag=$([ $((i % 4)) -eq 0 ] && echo "true" || echo "false")
  campaign_id=$(pick CAMPAIGNS)
  setup_time=$(rand_between 20 2000)
  ring_time=$(rand_between 0 20000)
  ivr_time=$(rand_between 0 60000)
  talk_time=$(rand_between 0 900000)
  hold_time=$(rand_between 0 60000)

  post_json "/api/live-calls" "{\"eventType\":\"${event_type}\",\"eventTimestamp\":\"${event_ts}\",\"crtObjectId\":\"c-${i}\",\"callId\":\"c-${i}\",\"callLegId\":\"cl-${i}\",\"campaignId\":${campaign_id},\"isOutbound\":false,\"callType\":\"${call_type}\",\"systemDisposition\":\"${system_disposition}\",\"callResult\":\"${call_result}\",\"hangupCause\":\"${hangup_cause}\",\"hangupCauseDescription\":\"${hangup_desc}\",\"hangupOnHold\":${hold_flag},\"callOriginateTime\":\"${orig_time}\",\"callEndTime\":${end_time},\"setupTime\":${setup_time},\"ringingTime\":${ring_time},\"ivrTime\":${ivr_time},\"talkTime\":${talk_time},\"holdTime\":${hold_time}}"
done

for i in $(seq 101 125); do
  start_time=$(iso_time $((i * 3 + 35)))
  end_time=$(iso_time $((i * 3 + 5)))
  hangup_cause=$(pick HANGUP_CAUSES)
  hangup_code=$(rand_between 16 200)
  setup_time=$(rand_between 20 2000)
  ring_time=$(rand_between 0 20000)
  talk_time=$(rand_between 0 1200000)
  post_json "/api/cm-cdr" "{\"callLegId\":\"cl-${i}\",\"hangupCause\":\"${hangup_cause}\",\"hangupCauseCode\":${hangup_code},\"setupTime\":${setup_time},\"ringTime\":${ring_time},\"talkTime\":${talk_time},\"startTime\":\"${start_time}\",\"endTime\":\"${end_time}\",\"voiceResourceInitializationTime\":\"${start_time}\",\"whichSideHungup\":\"CALLER\",\"internalHangupReason\":\"hangup.call.server.event\"}"
done

for i in $(seq 101 125); do
  disp_time=$(iso_time $((i * 4 + 30)))
  campaign_id=$(pick CAMPAIGNS)
  disposition_class=$(pick DISPOSITION_CLASSES)
  disposition_code=$(pick DISPOSITION_CODES)
  association_type=$(pick ASSOCIATION_TYPES)
  wrap_time=$(rand_between 0 600000)
  talk_time=$(rand_between 0 1200000)
  hold_time=$(rand_between 0 60000)
  cust_hold_time=$(rand_between 0 60000)

  post_json "/api/user-disposition" "{\"eventType\":\"DISPOSITION\",\"id\":\"ud-${i}\",\"callId\":\"c-${i}\",\"callLegId\":\"cl-${i}\",\"dateAdded\":\"${disp_time}\",\"userDispositionTime\":\"${disp_time}\",\"transferTime\":null,\"transferTo\":null,\"dispositionClass\":\"${disposition_class}\",\"dispositionCode\":\"${disposition_code}\",\"userId\":\"u-${i}\",\"sessionId\":\"s-${i}\",\"wrapTime\":${wrap_time},\"talkTime\":${talk_time},\"working\":true,\"disposedByCrm\":false,\"autoCallOnTime\":null,\"autoCallOffTime\":null,\"userConnectedTime\":\"${disp_time}\",\"userDisconnectedTime\":\"${disp_time}\",\"campaignId\":${campaign_id},\"associationType\":\"${association_type}\",\"customerHoldTime\":${cust_hold_time},\"holdTime\":${hold_time}}"
done

echo "Done. Sent 25 additional requests per endpoint (user-sessions: 25 LOGIN + 25 LOGOUT)."
