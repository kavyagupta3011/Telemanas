-- Additional queue queries we tried are listed below


-- Queues (Current Queue Status)
-- Counts calls that entered the queue in the last 10 minutes.
-- Excludes any call_id that later connected or disconnected.
-- Provides a single metric for current queue depth.
-- Intended for a Stat or Table panel.
SELECT
    COUNT(*) AS queue_count
FROM
    call_events
WHERE
    event_type = 'QUEUE_ENTERED'
    AND event_timestamp > NOW() - INTERVAL '10 minutes'
    AND call_id NOT IN (
        SELECT
            call_id
        FROM
            call_events
        WHERE
            event_type IN ('CONNECTED', 'DISCONNECTED')
    );

-- Calls Lost by Hour (Abandoned/Provider Failures)
-- Groups disconnect events by hour.
-- Filters to provider failures or hangups while on hold.
-- Returns the last 24 hourly buckets, newest first.
-- Intended for a bar chart by hour.
SELECT
    DATE_TRUNC('hour', event_timestamp) AS hour,
    COUNT(*) AS lost_calls
FROM
    call_events
WHERE
    event_type = 'DISCONNECTED'
    AND (system_disposition = 'PROVIDER_FAILURE' OR hangup_on_hold = true)
GROUP BY
    hour
ORDER BY
    hour DESC
LIMIT
    24;

-- Calls Handled (Successful Calls)
-- Counts successful calls that ended normally.
-- Uses disconnected events with success disposition.
-- Returns a single total value.
-- Intended for a Stat or Gauge panel.
SELECT
    COUNT(*) AS handled_calls
FROM
    call_events
WHERE
    event_type = 'DISCONNECTED'
    AND system_disposition = 'CONNECTED'
    AND call_result = 'SUCCESS';

-- How Much Time (Average Talk/Queue/Hold Time)
-- Calculates average durations for talk, queue, and hold.
-- Uses disconnected events as completed calls.
-- Returns three averages in one row.
-- Intended for a table or bar chart.
SELECT
    AVG(talk_time) AS avg_talk_time,
    AVG(queue_time) AS avg_queue_time,
    AVG(hold_time) AS avg_hold_time
FROM
    call_events
WHERE
    event_type = 'DISCONNECTED';

-- Call Back Requested (From Dispositions)
-- Counts callbacks requested by users.
-- Uses the disposition history table.
-- Filters to disposition_class = 'Callback'.
-- Intended for a Stat panel.
SELECT
    COUNT(*) AS callbacks_requested
FROM
    user_disposition_history
WHERE
    disposition_class = 'Callback';

-- Number of Hops (Transfers)
-- Counts transfer events.
-- Uses call_events transfer markers.
-- Returns a single total value.
-- Intended for a Stat panel.
SELECT
    COUNT(*) AS transfers
FROM
    call_events
WHERE
    event_type = 'TRANSFERRED';

-- Number of Hops (Conference Joins)
-- Counts conference join events.
-- Uses call_events conference markers.
-- Returns a single total value.
-- Intended for a Stat panel.
SELECT
    COUNT(*) AS conferences
FROM
    call_events
WHERE
    event_type = 'CONFERENCE_JOINED';

-- Mixing User and Calls (Agent-Call Correlations)
-- Joins user dispositions to call events by call_id.
-- Filters to connected calls.
-- Returns recent associations, newest first.
-- Intended for a table panel.
SELECT
    u.user_id,
    c.call_id,
    c.event_type,
    c.event_timestamp
FROM
    user_disposition_history u
    JOIN call_events c ON u.call_id = c.call_id
WHERE
    c.event_type = 'CONNECTED'
ORDER BY
    c.event_timestamp DESC
LIMIT
    50;

-- Actual Calls Right Now (Today by Hour)
-- Counts calls per hour for the current date.
-- Uses call_events timestamps truncated to hour.
-- Returns one row per hour.
-- Intended for a line chart.
SELECT
    DATE_TRUNC('hour', event_timestamp) AS hour,
    COUNT(*) AS calls
FROM
    call_events
WHERE
    DATE(event_timestamp) = CURRENT_DATE
GROUP BY
    hour;

-- Actual Calls (Yesterday by Hour)
-- Counts calls per hour for yesterday.
-- Uses call_events timestamps truncated to hour.
-- Returns one row per hour.
-- Intended for a line chart comparison.
SELECT
    DATE_TRUNC('hour', event_timestamp) AS hour,
    COUNT(*) AS calls
FROM
    call_events
WHERE
    DATE(event_timestamp) = CURRENT_DATE - 1
GROUP BY
    hour;

-- Queue Distribution (Bucketed Queue Time)
-- Buckets queue_time into ranges.
-- Counts calls per bucket.
-- Uses disconnected events to reflect completed calls.
-- Intended for a histogram or bar chart.
SELECT
    CASE
        WHEN queue_time < 120000 THEN '0-2 min'
        WHEN queue_time < 300000 THEN '2-5 min'
        WHEN queue_time < 600000 THEN '5-10 min'
        ELSE '10+ min'
    END AS queue_bucket,
    COUNT(*) AS count
FROM
    call_events
WHERE
    event_type = 'DISCONNECTED'
GROUP BY
    queue_bucket;

-- User Available (Available Agents by Hour)
-- Counts ready events per hour.
-- Uses agent_activity ready start times.
-- Returns one row per hour.
-- Intended for a line chart.
SELECT
    DATE_TRUNC('hour', ready_start_time) AS hour,
    COUNT(*) AS available_agents
FROM
    agent_activity
WHERE
    event_type = 'AGENT_SET_READY'
GROUP BY
    hour;

-- Call Density (Connected Calls by Hour)
-- Counts connected calls per hour.
-- Uses call_events timestamps truncated to hour.
-- Returns one row per hour.
-- Intended for a line chart.
SELECT
    DATE_TRUNC('hour', event_timestamp) AS hour,
    COUNT(*) AS calls
FROM
    call_events
WHERE
    event_type = 'CONNECTED'
GROUP BY
    hour;

-- Counsellors Involved (Distinct Users)
-- Counts distinct users in disposition history.
-- Measures total counsellors involved.
-- Returns a single total value.
-- Intended for a Stat panel.
SELECT
    COUNT(DISTINCT user_id) AS counsellors_involved
FROM
    user_disposition_history;

-- Counsellors Idle Now (Currently Ready)
-- Counts agents marked ready with no end time.
-- Assumes NULL ready_end_time means still ready.
-- Returns a single total value.
-- Intended for a Stat panel.
SELECT
    COUNT(*) AS idle_counsellors
FROM
    agent_activity
WHERE
    event_type = 'AGENT_SET_READY'
    AND ready_end_time IS NULL;
