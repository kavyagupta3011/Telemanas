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
BREAK_REASONS=("Lunch" "Tea" "Snacks" "Meeting" "Unavailable" "System" "Training" "Short Break")
AGENT_BREAK_REASONS=("Just.Logged.In" "erroneous.channel.system.initiated.break" "NORMAL" "Timeout" "Manual")

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

# Extra agent-activity records with varied break durations
for i in $(seq 201 300); do
  campaign_id=$(pick CAMPAIGNS)
  ready_start=$(iso_time $((i * 3 + 120)))
  ready_end=$(iso_time $((i * 3 + 60)))

  # break duration in minutes: 2 to 45
  break_minutes=$(rand_between 2 45)
  break_end=$(iso_time $((i * 3 + 60 - break_minutes)))

  break_reason=$(pick BREAK_REASONS)
  agent_break_reason=$(pick AGENT_BREAK_REASONS)

  post_json "/api/agent-activity" "{\"sessionId\":\"s-${i}\",\"campaignId\":${campaign_id},\"readyStartTime\":\"${ready_start}\",\"readyEndTime\":\"${ready_end}\",\"breakEndTime\":\"${break_end}\",\"breakReason\":\"${break_reason}\",\"agentBreakReason\":\"${agent_break_reason}\",\"eventType\":\"AGENT_SET_NOT_READY\"}"
done

echo "Done. Sent 100 extra agent-activity break records (IDs 201-300)."
