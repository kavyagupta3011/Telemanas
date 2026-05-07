-- Ready Agents Over Time
-- Visualization: Time series | Source: agent_activity
-- Builds 5s time buckets for the dashboard range, joins ready intervals that cover each bucket (NULL end treated as NOW()), and outputs time + concurrent ready_agents.
WITH buckets AS (
  SELECT
    generate_series($__timeFrom()::timestamptz, $__timeTo()::timestamptz, '5s'::interval) AS bucket_start
)
SELECT
  bucket_start AS "time",
  COUNT(*) AS ready_agents
FROM
  buckets b
LEFT JOIN agent_activity a
  ON a.ready_start_time IS NOT NULL
  AND a.ready_start_time <= b.bucket_start
  AND COALESCE(a.ready_end_time, NOW()) > b.bucket_start
GROUP BY
  1
ORDER BY
  1;

-- Autocall Starts by Campaign
-- Visualization: Bar chart | Source: autocall_activity
-- Filters rows by auto_call_on_start_time in the dashboard range, groups by campaign_id, and outputs campaign_id + total autocall starts for comparison.
SELECT
  campaign_id::text AS campaign_id,
  COUNT(*) AS calls
FROM
  autocall_activity
WHERE
  auto_call_on_start_time IS NOT NULL
  AND $__timeFilter(auto_call_on_start_time)
GROUP BY
  campaign_id
ORDER BY
  calls DESC;

-- Active Sessions Now
-- Visualization: Stat | Source: user_sessions
-- Counts rows where logout_time IS NULL to show currently open sessions, with no time filter so it reflects the live active session count.
SELECT
  COUNT(*) AS active_now
FROM
  user_sessions
WHERE
  logout_time IS NULL;

-- Logins Over Time
-- Visualization: Time series | Source: user_sessions
-- Buckets login_time into 5-minute intervals using the dashboard range and outputs time + login count to visualize login volume over time.
SELECT
  $__timeGroupAlias(login_time, '5m'),
  COUNT(*) AS logins
FROM
  user_sessions
WHERE
  $__timeFilter(login_time)
GROUP BY
  1
ORDER BY
  1;

-- Logouts Over Time
-- Visualization: Time series | Source: user_sessions
-- Filters to non-null logout_time, buckets into 5-minute intervals over the dashboard range, and outputs time + logout count to show logout trends.
SELECT
  $__timeGroupAlias(logout_time, '5m'),
  COUNT(*) AS logouts
FROM
  user_sessions
WHERE
  logout_time IS NOT NULL
  AND $__timeFilter(logout_time)
GROUP BY
  1
ORDER BY
  1;

-- Avg Session Duration (min)
-- Visualization: Time series | Source: user_sessions
-- Computes average session duration per 15-minute bucket using (COALESCE(logout_time, NOW()) - login_time) and outputs time + avg_session_mins to show typical duration.
SELECT
  $__timeGroupAlias(login_time, '15m'),
  AVG(EXTRACT(EPOCH FROM (COALESCE(logout_time, NOW()) - login_time)) / 60.0) AS avg_session_mins
FROM
  user_sessions
WHERE
  $__timeFilter(login_time)
GROUP BY
  1
ORDER BY
  1;

-- P95 Session Duration (min)
-- Visualization: Time series | Source: user_sessions
-- Calculates the 95th percentile of session duration per 30-minute bucket using (COALESCE(logout_time, NOW()) - login_time) and outputs time + p95_session_mins to highlight long sessions.
SELECT
  $__timeGroupAlias(login_time, '30m'),
  percentile_cont(0.95) WITHIN GROUP (
    ORDER BY EXTRACT(EPOCH FROM (COALESCE(logout_time, NOW()) - login_time)) / 60.0
  ) AS p95_session_mins
FROM
  user_sessions
WHERE
  $__timeFilter(login_time)
GROUP BY
  1
ORDER BY
  1;

-- Session Reason Distribution
-- Visualization: Pie chart | Source: user_sessions
-- Groups sessions by reason (NULL -> UNKNOWN) within the login_time range and outputs reason + count to show the reason mix.
SELECT
  COALESCE(reason, 'UNKNOWN') AS reason,
  COUNT(*) AS value
FROM
  user_sessions
WHERE
  $__timeFilter(login_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Top Users by Session Count
-- Visualization: Bar chart | Source: user_sessions
-- Groups by user_id within the login_time range and outputs the top 10 users by session count to identify heavy users.
SELECT
  user_id,
  COUNT(*) AS sessions
FROM
  user_sessions
WHERE
  $__timeFilter(login_time)
GROUP BY
  user_id
ORDER BY
  sessions DESC
LIMIT
  10;

-- Concurrent Sessions (Estimated)
-- Visualization: Time series | Source: user_sessions
-- Converts logins to +1 and logouts to -1 per hour bucket, then takes a running sum to output time + estimated concurrent_sessions.
WITH points AS (
  SELECT
    date_trunc('hour', login_time) AS t,
    1 AS delta
  FROM
    user_sessions
  WHERE
    $__timeFilter(login_time)
  UNION ALL
  SELECT
    date_trunc('hour', logout_time) AS t,
    -1 AS delta
  FROM
    user_sessions
  WHERE
    logout_time IS NOT NULL
    AND $__timeFilter(logout_time)
),
agg AS (
  SELECT
    t,
    SUM(delta) AS delta
  FROM
    points
  GROUP BY
    t
)
SELECT
  t AS "time",
  SUM(delta) OVER (ORDER BY t) AS concurrent_sessions
FROM
  agg
ORDER BY
  t;

-- Agent Ready Intervals Over Time
-- Visualization: Time series | Source: agent_activity
-- Buckets ready_start_time into 10-minute intervals over the dashboard range and outputs time + ready_intervals to show readiness activity volume.
SELECT
  $__timeGroupAlias(ready_start_time, '10m'),
  COUNT(*) AS ready_intervals
FROM
  agent_activity
WHERE
  $__timeFilter(ready_start_time)
GROUP BY
  1
ORDER BY
  1;

-- Avg Agent Ready Duration (min)
-- Visualization: Time series | Source: agent_activity
-- Computes average ready duration per 15-minute bucket using (COALESCE(ready_end_time, NOW()) - ready_start_time) and outputs time + avg_ready_mins.
SELECT
  $__timeGroupAlias(ready_start_time, '15m'),
  AVG(EXTRACT(EPOCH FROM (COALESCE(ready_end_time, NOW()) - ready_start_time)) / 60.0) AS avg_ready_mins
FROM
  agent_activity
WHERE
  $__timeFilter(ready_start_time)
GROUP BY
  1
ORDER BY
  1;

-- Agent Ready Duration P95 (min)
-- Visualization: Time series | Source: agent_activity
-- Calculates the 95th percentile of ready duration per 30-minute bucket and outputs time + p95_ready_mins to spotlight long ready intervals.
SELECT
  $__timeGroupAlias(ready_start_time, '30m'),
  percentile_cont(0.95) WITHIN GROUP (
    ORDER BY EXTRACT(EPOCH FROM (COALESCE(ready_end_time, NOW()) - ready_start_time)) / 60.0
  ) AS p95_ready_mins
FROM
  agent_activity
WHERE
  $__timeFilter(ready_start_time)
GROUP BY
  1
ORDER BY
  1;

-- Break Reason Distribution
-- Visualization: Bar chart | Source: agent_activity
-- Groups by break_reason (NULL -> UNKNOWN) within the ready_start_time range and outputs break_reason + count to show break reasons.
SELECT
  COALESCE(break_reason, 'UNKNOWN') AS break_reason,
  COUNT(*) AS value
FROM
  agent_activity
WHERE
  break_end_time IS NOT NULL
  AND $__timeFilter(break_end_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Avg Break Duration by Reason (min)
-- Visualization: Bar chart | Source: agent_activity
-- Computes average break duration per break_reason using (COALESCE(break_end_time, NOW()) - ready_end_time) and outputs reason + avg_break_mins.
SELECT
  COALESCE(break_reason, 'UNKNOWN') AS break_reason,
  AVG(EXTRACT(EPOCH FROM (COALESCE(break_end_time, NOW()) - ready_end_time)) / 60.0) AS avg_break_mins
FROM
  agent_activity
WHERE
  ready_end_time IS NOT NULL
  AND $__timeFilter(ready_end_time)
GROUP BY
  1
ORDER BY
  avg_break_mins DESC;

-- Agent Activity by Campaign
-- Visualization: Time series | Source: agent_activity
-- Buckets ready_start_time into 15-minute intervals, groups by campaign_id, and outputs time + campaign series for activity trends.
SELECT
  $__timeGroupAlias(ready_start_time, '15m'),
  campaign_id::text AS metric,
  COUNT(*) AS value
FROM
  agent_activity
WHERE
  $__timeFilter(ready_start_time)
GROUP BY
  1,
  2
ORDER BY
  1;

-- Distinct Sessions by Campaign
-- Visualization: Bar chart | Source: agent_activity
-- Counts distinct session_id per campaign within the ready_start_time range and outputs campaign + distinct_sessions for unique session comparison.
SELECT
  campaign_id::text AS campaign,
  COUNT(DISTINCT session_id) AS distinct_sessions
FROM
  agent_activity
WHERE
  $__timeFilter(ready_start_time)
GROUP BY
  campaign_id
ORDER BY
  distinct_sessions DESC;

-- Longest Ready Intervals
-- Visualization: Table | Source: agent_activity
-- Calculates ready duration per row (COALESCE(ready_end_time, NOW()) - ready_start_time) and outputs the longest intervals for inspection.
SELECT
  id,
  campaign_id,
  session_id,
  ready_start_time,
  ready_end_time,
  EXTRACT(EPOCH FROM (COALESCE(ready_end_time, NOW()) - ready_start_time)) / 60.0 AS ready_mins
FROM
  agent_activity
WHERE
  $__timeFilter(ready_start_time)
ORDER BY
  ready_mins DESC
LIMIT
  25;

-- Autocall Starts Over Time
-- Visualization: Time series | Source: autocall_activity
-- Buckets auto_call_on_start_time into 10-minute intervals over the dashboard range and outputs time + autocall_starts to show start volume.
SELECT
  $__timeGroupAlias(auto_call_on_start_time, '10m'),
  COUNT(*) AS autocall_starts
FROM
  autocall_activity
WHERE
  auto_call_on_start_time IS NOT NULL
  AND $__timeFilter(auto_call_on_start_time)
GROUP BY
  1
ORDER BY
  1;

-- Avg Autocall ON Duration (min)
-- Visualization: Time series | Source: autocall_activity
-- Computes average ON duration per 15-minute bucket using (COALESCE(auto_call_on_end_time, NOW()) - auto_call_on_start_time) and outputs time + avg_on_mins.
SELECT
  $__timeGroupAlias(auto_call_on_start_time, '15m'),
  AVG(EXTRACT(EPOCH FROM (COALESCE(auto_call_on_end_time, NOW()) - auto_call_on_start_time)) / 60.0) AS avg_on_mins
FROM
  autocall_activity
WHERE
  auto_call_on_start_time IS NOT NULL
  AND $__timeFilter(auto_call_on_start_time)
GROUP BY
  1
ORDER BY
  1;

-- P95 Autocall ON Duration (min)
-- Visualization: Time series | Source: autocall_activity
-- Calculates the 95th percentile of ON duration per 30-minute bucket and outputs time + p95_on_mins to highlight long sessions.
SELECT
  $__timeGroupAlias(auto_call_on_start_time, '30m'),
  percentile_cont(0.95) WITHIN GROUP (
    ORDER BY EXTRACT(EPOCH FROM (COALESCE(auto_call_on_end_time, NOW()) - auto_call_on_start_time)) / 60.0
  ) AS p95_on_mins
FROM
  autocall_activity
WHERE
  auto_call_on_start_time IS NOT NULL
  AND $__timeFilter(auto_call_on_start_time)
GROUP BY
  1
ORDER BY
  1;

-- Autocall OFF Lag After ON End (sec)
-- Visualization: Time series | Source: autocall_activity
-- Computes average lag (auto_call_off_end_time - auto_call_on_end_time) per bucket, using NOW() if off end is NULL, outputting time + avg_off_lag_sec.
SELECT
  $__timeGroupAlias(auto_call_on_end_time, '15m'),
  AVG(EXTRACT(EPOCH FROM (COALESCE(auto_call_off_end_time, NOW()) - auto_call_on_end_time))) AS avg_off_lag_sec
FROM
  autocall_activity
WHERE
  auto_call_on_end_time IS NOT NULL
  AND $__timeFilter(auto_call_on_end_time)
GROUP BY
  1
ORDER BY
  1;

-- Autocall End Reason Distribution
-- Visualization: Bar chart | Source: autocall_activity
-- Groups by end_reason (NULL -> UNKNOWN) within the auto_call_on_start_time range and outputs end_reason + count to show why calls ended.
SELECT
  COALESCE(end_reason, 'UNKNOWN') AS end_reason,
  COUNT(*) AS value
FROM
  autocall_activity
WHERE
  auto_call_on_start_time IS NOT NULL
  AND $__timeFilter(auto_call_on_start_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Autocall Start Reason Distribution
-- Visualization: Pie chart | Source: autocall_activity
-- Groups by auto_call_start_reason (NULL -> UNKNOWN) within the auto_call_on_start_time range and outputs start_reason + count.
SELECT
  COALESCE(auto_call_start_reason, 'UNKNOWN') AS start_reason,
  COUNT(*) AS value
FROM
  autocall_activity
WHERE
  auto_call_on_start_time IS NOT NULL
  AND $__timeFilter(auto_call_on_start_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Autocall Throughput by Campaign
-- Visualization: Time series | Source: autocall_activity
-- Buckets auto_call_on_start_time into 15-minute intervals, groups by campaign_id, and outputs time + campaign series to compare throughput.
SELECT
  $__timeGroupAlias(auto_call_on_start_time, '15m'),
  campaign_id::text AS metric,
  COUNT(*) AS value
FROM
  autocall_activity
WHERE
  auto_call_on_start_time IS NOT NULL
  AND $__timeFilter(auto_call_on_start_time)
GROUP BY
  1,
  2
ORDER BY
  1;

-- Total Calls per Campaign
-- Visualization: Table | Source: live_calls
-- Counts all calls per campaign within the call_originate_time range and outputs campaign_id + calls for volume comparison.
SELECT
  campaign_id,
  COUNT(*) AS calls
FROM
  live_calls
WHERE
  $__timeFilter(call_originate_time)
GROUP BY
  campaign_id
ORDER BY
  calls DESC;

-- Live Calls by Current State
-- Visualization: Bar chart | Source: live_calls
-- Groups calls by current_state within the call_originate_time range and outputs state + count to show state distribution.
SELECT
  current_state,
  COUNT(*) AS value
FROM
  live_calls
WHERE
  $__timeFilter(call_originate_time)
GROUP BY
  current_state
ORDER BY
  value DESC;

-- Avg Talk Time by Campaign (sec)
-- Visualization: Bar chart | Source: live_calls
-- Averages talk_time per campaign within the call_originate_time range and outputs campaign_id + avg_talk_sec for call length comparison.
SELECT
  campaign_id::text AS campaign_id,
  AVG(talk_time) AS avg_talk_sec
FROM
  live_calls
WHERE
  talk_time IS NOT NULL
  AND $__timeFilter(call_originate_time)
GROUP BY
  campaign_id
ORDER BY
  avg_talk_sec DESC;

-- CDR Calls Over Time
-- Visualization: Time series | Source: cm_cdr_history
-- Buckets start_time into 10-minute intervals over the dashboard range and outputs time + cdr_calls to show CDR volume.
SELECT
  $__timeGroupAlias(start_time, '10m'),
  COUNT(*) AS cdr_calls
FROM
  cm_cdr_history
WHERE
  start_time IS NOT NULL
  AND $__timeFilter(start_time)
GROUP BY
  1
ORDER BY
  1;

-- CDR Hangup Cause Distribution
-- Visualization: Bar chart | Source: cm_cdr_history
-- Groups by hangup_cause within the start_time range and outputs hangup_cause + count to show hangup distribution.
SELECT
  COALESCE(hangup_cause, 'UNKNOWN') AS hangup_cause,
  COUNT(*) AS value
FROM
  cm_cdr_history
WHERE
  $__timeFilter(start_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- User Dispositions Over Time
-- Visualization: Time series | Source: user_disposition_history
-- Buckets user_disposition_time into 10-minute intervals over the dashboard range and outputs time + dispositions to show disposition volume.
SELECT
  $__timeGroupAlias(user_disposition_time, '10m'),
  COUNT(*) AS dispositions
FROM
  user_disposition_history
WHERE
  user_disposition_time IS NOT NULL
  AND $__timeFilter(user_disposition_time)
GROUP BY
  1
ORDER BY
  1;

-- Disposition Code Distribution
-- Visualization: Bar chart | Source: user_disposition_history
-- Groups by disposition_code within the user_disposition_time range and outputs disposition_code + count to show code distribution.
SELECT
  COALESCE(disposition_code, 'UNKNOWN') AS disposition_code,
  COUNT(*) AS value
FROM
  user_disposition_history
WHERE
  $__timeFilter(user_disposition_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Live Calls Throughput
-- Visualization: Time series | Source: live_calls
-- Buckets call_originate_time into 5-minute intervals over the dashboard range and outputs time + calls to show inbound/outbound volume over time.
SELECT
  $__timeGroupAlias(call_originate_time, '5m'),
  COUNT(*) AS calls
FROM
  live_calls
WHERE
  $__timeFilter(call_originate_time)
GROUP BY
  1
ORDER BY
  1;

-- Live Calls by Call Type
-- Visualization: Bar chart | Source: live_calls
-- Groups by call_type within the call_originate_time range and outputs call_type + count to show type mix.
SELECT
  COALESCE(call_type, 'UNKNOWN') AS call_type,
  COUNT(*) AS value
FROM
  live_calls
WHERE
  $__timeFilter(call_originate_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Live Calls by Call Result
-- Visualization: Bar chart | Source: live_calls
-- Groups by call_result within the call_originate_time range and outputs call_result + count to show success vs failure distribution.
SELECT
  COALESCE(call_result, 'UNKNOWN') AS call_result,
  COUNT(*) AS value
FROM
  live_calls
WHERE
  $__timeFilter(call_originate_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Live Calls Avg Timing (sec)
-- Visualization: Time series | Source: live_calls
-- Buckets call_originate_time into 10-minute intervals and outputs avg timing metrics (setup, ringing, ivr, talk) to track quality trends.
SELECT
  $__timeGroupAlias(call_originate_time, '10m'),
  AVG(setup_time) AS avg_setup_sec,
  AVG(ringing_time) AS avg_ringing_sec,
  AVG(ivr_time) AS avg_ivr_sec,
  AVG(talk_time) AS avg_talk_sec
FROM
  live_calls
WHERE
  $__timeFilter(call_originate_time)
GROUP BY
  1
ORDER BY
  1;

-- Break Events Over Time
-- Visualization: Time series | Source: agent_activity
-- Buckets break_end_time into 10-minute intervals and outputs time + breaks to show break frequency over time.
SELECT
  $__timeGroupAlias(break_end_time, '10m'),
  COUNT(*) AS breaks
FROM
  agent_activity
WHERE
  break_end_time IS NOT NULL
  AND $__timeFilter(break_end_time)
GROUP BY
  1
ORDER BY
  1;

-- Agent Break Reason vs System Reason
-- Visualization: Table | Source: agent_activity
-- Groups by break_reason and agent_break_reason in the time range to show how system and agent labels align.
SELECT
  COALESCE(break_reason, 'UNKNOWN') AS break_reason,
  COALESCE(agent_break_reason, 'UNKNOWN') AS agent_break_reason,
  COUNT(*) AS value
FROM
  agent_activity
WHERE
  break_end_time IS NOT NULL
  AND $__timeFilter(break_end_time)
GROUP BY
  1,
  2
ORDER BY
  value DESC;

-- CDR Talk Time Over Time (sec)
-- Visualization: Time series | Source: cm_cdr_history
-- Buckets start_time into 10-minute intervals and outputs time + avg_talk_sec to show call duration trends.
SELECT
  $__timeGroupAlias(start_time, '10m'),
  AVG(talk_time) AS avg_talk_sec
FROM
  cm_cdr_history
WHERE
  start_time IS NOT NULL
  AND $__timeFilter(start_time)
GROUP BY
  1
ORDER BY
  1;

-- CDR Hangup Cause Code Distribution
-- Visualization: Table | Source: cm_cdr_history
-- Groups by hangup_cause_code within the start_time range and outputs code + count to show failure patterns.
SELECT
  hangup_cause_code,
  COUNT(*) AS value
FROM
  cm_cdr_history
WHERE
  hangup_cause_code IS NOT NULL
  AND $__timeFilter(start_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- CDR Which Side Hung Up
-- Visualization: Pie chart | Source: cm_cdr_history
-- Groups by which_side_hungup within the start_time range and outputs side + count to show caller vs callee hangups.
SELECT
  COALESCE(which_side_hungup, 'UNKNOWN') AS which_side_hungup,
  COUNT(*) AS value
FROM
  cm_cdr_history
WHERE
  $__timeFilter(start_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Dispositions by Class
-- Visualization: Gauge | Source: user_disposition_history
-- Groups by disposition_class within the user_disposition_time range and outputs class + count to show outcome categories.
SELECT
  COALESCE(disposition_class, 'UNKNOWN') AS disposition_class,
  COUNT(*) AS value
FROM
  user_disposition_history
WHERE
  $__timeFilter(user_disposition_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Dispositions by Campaign
-- Visualization: Pie chart | Source: user_disposition_history
-- Groups by campaign_id within the user_disposition_time range and outputs campaign_id + count to compare campaign outcomes.
SELECT
  campaign_id::text AS campaign_id,
  COUNT(*) AS value
FROM
  user_disposition_history
WHERE
  campaign_id IS NOT NULL
  AND $__timeFilter(user_disposition_time)
GROUP BY
  1
ORDER BY
  value DESC;

-- Avg Wrap Time by Disposition Code (sec)
-- Visualization: Bar gauge | Source: user_disposition_history
-- Groups by disposition_code in the time range and outputs code + avg_wrap_sec to compare after-call work time.
SELECT
  COALESCE(disposition_code, 'UNKNOWN') AS disposition_code,
  AVG(wrap_time) AS avg_wrap_sec
FROM
  user_disposition_history
WHERE
  wrap_time IS NOT NULL
  AND $__timeFilter(user_disposition_time)
GROUP BY
  1
ORDER BY
  avg_wrap_sec DESC;



