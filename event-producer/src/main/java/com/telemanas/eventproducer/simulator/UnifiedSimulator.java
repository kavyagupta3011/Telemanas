package com.telemanas.eventproducer.simulator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.telemanas.eventproducer.model.AgentActivityEvent;
import com.telemanas.eventproducer.model.AutoCallEvent;
import com.telemanas.eventproducer.model.CallEvent;
import com.telemanas.eventproducer.model.CmCdrEvent;
import com.telemanas.eventproducer.model.UserDisposition;   
import com.telemanas.eventproducer.model.UserSessionEvent;
import com.telemanas.eventproducer.service.AgentActivityProducerService;
import com.telemanas.eventproducer.service.AutoCallProducerService;
import com.telemanas.eventproducer.service.CallProducerService;
import com.telemanas.eventproducer.service.CmCdrProducerService;
import com.telemanas.eventproducer.service.UserDispositionProducerService; 
import com.telemanas.eventproducer.service.UserSessionProducerService;

@Component
public class UnifiedSimulator implements CommandLineRunner {

    // ── Services ────────────────────────────────────────────────────────
    private final UserSessionProducerService    sessionProducer;
    private final AgentActivityProducerService  activityProducer;
    private final AutoCallProducerService       autoCallProducer;
    private final CallProducerService           callProducer;
    private final CmCdrProducerService            cdrProducer;
    private final UserDispositionProducerService   dispositionProducer;

    // ── Agent State ──────────────────────────────────────────────────────
    // Tracks all agents who are currently logged in
    private final Set<String>                       activeAgents        = ConcurrentHashMap.newKeySet();
    // The "available for call" pool — now carries full context per agent
    private final BlockingQueue<AgentContext>        availableAgents     = new LinkedBlockingQueue<>();
    // Per-agent flag: true while the system has auto-broken this agent
    private final ConcurrentHashMap<String, AtomicBoolean> erroneousBreakFlags  = new ConcurrentHashMap<>();
    // Stores the most recent AgentContext per user for re-insertion after erroneous break
    private final ConcurrentHashMap<String, AgentContext>  latestAgentContexts  = new ConcurrentHashMap<>();

    /**
     * AgentContext — passed through the availableAgents queue.
     * Carries everything a call handler needs to generate disposition events
     * without needing to look anything up separately.
     */
    private static class AgentContext {
        final String  userId;
        final String  sessionId;
        final Instant autoCallOnTime;

        AgentContext(String userId, String sessionId, Instant autoCallOnTime) {
            this.userId        = userId;
            this.sessionId     = sessionId;
            this.autoCallOnTime = autoCallOnTime;
        }
    }

    // ── Disposition outcomes (class → possible codes) ────────────────────
    private static final Map<String, List<String>> DISPOSITION_MAP = Map.of(
        "Success",       List.of("Counselling Completed", "Referral Given", "Follow Up Scheduled"),
        "Callback",      List.of("Callback Requested", "No Answer - Rescheduled", "Busy - Will Retry"),
        "Emergency",     List.of("Emergency Referred to Hospital", "Crisis Intervention"),
        "Informational", List.of("Information Provided", "Awareness Call - No Action Needed"),
        "Prank",         List.of("Prank Call", "Silent Call", "Abusive Caller")
    );
    private static final List<String> DISPOSITION_CLASSES = new ArrayList<>(DISPOSITION_MAP.keySet());

    // ── Campaign → State mapping ─────────────────────────────────────────
    private static final Map<Integer, String> STATE_MAP = Map.ofEntries(
        Map.entry(3,  "Andhra Pradesh"),       Map.entry(10, "Chandigarh"),
        Map.entry(11, "Chhattisgarh"),         Map.entry(12, "Ladakh"),
        Map.entry(13, "Manipur"),              Map.entry(14, "Mizoram"),
        Map.entry(15, "Odisha"),               Map.entry(16, "Pondicherry"),
        Map.entry(17, "Punjab"),               Map.entry(18, "Rajasthan"),
        Map.entry(19, "Uttarakhand"),          Map.entry(20, "Delhi"),
        Map.entry(21, "Uttar Pradesh"),        Map.entry(22, "Haryana"),
        Map.entry(23, "Telangana"),            Map.entry(24, "Assam"),
        Map.entry(25, "Bihar"),                Map.entry(26, "Jharkhand"),
        Map.entry(27, "Tamil Nadu"),           Map.entry(28, "Gujarat"),
        Map.entry(29, "Dadra & Daman & Diu"),  Map.entry(30, "Himachal Pradesh"),
        Map.entry(31, "Jammu & Kashmir"),      Map.entry(32, "Maharashtra"),
        Map.entry(33, "Kerala"),               Map.entry(34, "Lakshadweep"),
        Map.entry(35, "Karnataka"),            Map.entry(36, "Andaman & Nicobar"),
        Map.entry(37, "Goa"),                  Map.entry(38, "West Bengal"),
        Map.entry(39, "Madhya Pradesh"),       Map.entry(40, "Sikkim"),
        Map.entry(41, "Arunachal Pradesh"),    Map.entry(42, "Tripura"),
        Map.entry(43, "Meghalaya"),            Map.entry(44, "Nagaland"),
        Map.entry(47, "AFMS")
    );
    private static final List<Integer> CAMPAIGN_IDS = new ArrayList<>(STATE_MAP.keySet());

    // ── Constructor ──────────────────────────────────────────────────────
    public UnifiedSimulator(
            UserSessionProducerService   sessionProducer,
            AgentActivityProducerService activityProducer,
            AutoCallProducerService      autoCallProducer,
            CallProducerService          callProducer,
            CmCdrProducerService           cdrProducer,
            UserDispositionProducerService   dispositionProducer) {
        this.sessionProducer     = sessionProducer;
        this.activityProducer    = activityProducer;
        this.autoCallProducer    = autoCallProducer;
        this.callProducer        = callProducer;
        this.cdrProducer         = cdrProducer;
        this.dispositionProducer = dispositionProducer;
    }

    // ENTRY POINT
    @Override
    public void run(String... args) throws Exception {
        System.out.println(" Starting FULL-TABLE Call Center Simulation...");

        List<String> users = List.of(
            "user1","user2","user3","user4","user5","user6","user7","user8","user9","user10",
            "user11","user12","user13","user14","user15","user16","user17","user18","user19","user20"
        );

        // Pre-create erroneous break flags so they are ready before any thread starts
        for (String user : users) {
            erroneousBreakFlags.put(user, new AtomicBoolean(false));
        }

        ExecutorService agentPool = Executors.newFixedThreadPool(users.size());
        ExecutorService callPool  = Executors.newCachedThreadPool();

        // Boot each agent's independent shift loop
        for (String user : users) {
            agentPool.submit(() -> runAgentShift(user));
        }

        // Boot the inbound call generator
        new Thread(() -> generateInboundCalls(callPool)).start();
    }

    // THE AGENT BRAIN — runs as a continuous loop (shift → break → shift)
    private void runAgentShift(String user) {
        try {
            while (true) {
                // Stagger initial logins so they don't all fire at t=0
                Thread.sleep(ThreadLocalRandom.current().nextLong(2000, 15000));

                String sessionId = "sess-" + UUID.randomUUID().toString().substring(0, 8);
                AtomicBoolean onErroneousBreak = erroneousBreakFlags.get(user);
                onErroneousBreak.set(false);

                // ── user_sessions: LOGIN ─────────────────────────────────
                sendSessionEvent(user, sessionId, "LOGIN", null);
                activeAgents.add(user);
                System.out.println(" [AGENT] " + user + " logged in. Session=" + sessionId);

                // ── user_readiness: READY (after Just.Logged.In delay) ───
                Thread.sleep(ThreadLocalRandom.current().nextLong(2000, 5000));

                AgentActivityEvent activityEvent = new AgentActivityEvent();
                activityEvent.setSessionId(sessionId);
                activityEvent.setCampaignId(101);
                activityEvent.setReadyStartTime(Instant.now());
                activityEvent.setBreakEndTime(Instant.now());
                activityEvent.setEventType("AGENT_SET_READY");
                activityEvent.setAgentBreakReason("Just.Logged.In");
                activityProducer.send(activityEvent);
                System.out.println(" [AGENT] " + user + " is READY.");

                // ── auto_call: AutoCall ON ───────────────────────────────
                Thread.sleep(ThreadLocalRandom.current().nextLong(2000, 5000));
                Instant autoCallOnTime = Instant.now();

                AutoCallEvent autoCallEvent = new AutoCallEvent();
                autoCallEvent.setId("ac-" + UUID.randomUUID().toString().substring(0, 8));
                autoCallEvent.setSessionId(sessionId);
                autoCallEvent.setCampaignId(101);
                autoCallEvent.setAutoCallStartReason("System Trigger");
                autoCallEvent.setAutoCallOnStartTime(autoCallOnTime);
                autoCallProducer.send(autoCallEvent);
                System.out.println(" [AGENT] " + user + " AutoCall ON.");

                // Agent is now fully operational — join the available pool
                AgentContext ctx = new AgentContext(user, sessionId, autoCallOnTime);
                latestAgentContexts.put(user, ctx);
                availableAgents.put(ctx);

                // Start the erroneous-break monitor for this shift (daemon thread)
                Thread breakMonitor = new Thread(
                    () -> runErroneousBreakMonitor(user, sessionId, activityEvent, onErroneousBreak));
                breakMonitor.setDaemon(true);
                breakMonitor.start();

                // Agent works for 1–3 minutes before shift ends
                Thread.sleep(ThreadLocalRandom.current().nextLong(60000, 180000));

                // ── Shift ends: teardown ─────────────────────────────────
                breakMonitor.interrupt();
                activeAgents.remove(user);
                availableAgents.removeIf(c -> c.userId.equals(user));
                Instant shiftEndTime = Instant.now();

                // auto_call: AutoCall OFF
                autoCallEvent.setAutoCallOnEndTime(shiftEndTime);
                autoCallEvent.setAutoCallOffEndTime(shiftEndTime.plusSeconds(2));
                autoCallEvent.setEndReason("Batch Completed");
                autoCallProducer.send(autoCallEvent);
                System.out.println(" [AGENT] " + user + " AutoCall OFF.");

                // user_readiness: READY state ended
                activityEvent.setReadyEndTime(shiftEndTime.plusSeconds(2));
                String breakReason = pickRandom(List.of(
                    "Shift Ended", "Shift Ended", "Shift Ended",
                    "Lunch Break", "Tea Break", "Meeting", "Training", "System Issue"
                ));
                activityEvent.setBreakReason(breakReason);
                activityProducer.send(activityEvent);
                System.out.println(" [AGENT] " + user + " READY state ended (" + breakReason + ").");

                // user_sessions: LOGOUT
                String logoutReason = pickRandom(List.of(
                    "Logout from UI", "Logout from UI", "Logout from UI",
                    "urgent work", "Session TimeOut", "Forced termination"
                ));
                sendSessionEvent(user, sessionId, "LOGOUT", logoutReason);
                System.out.println(" [AGENT] " + user + " logged out (" + logoutReason + ").");

                // Rest between shifts
                Thread.sleep(ThreadLocalRandom.current().nextLong(30000, 60000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    
    // ERRONEOUS BREAK MONITOR
    // Runs concurrently alongside each agent's shift.
    // Simulates: agent misses/rejects calls repeatedly → system forces a break.
    private void runErroneousBreakMonitor(
            String user,
            String sessionId,
            AgentActivityEvent baseActivityEvent,
            AtomicBoolean onErroneousBreak) {
        try {
            while (activeAgents.contains(user) && !Thread.currentThread().isInterrupted()) {

                // Check every 30–60 seconds
                Thread.sleep(ThreadLocalRandom.current().nextLong(30000, 60000));

                if (!activeAgents.contains(user)) break;

                // 15% chance of triggering a forced erroneous break
                if (ThreadLocalRandom.current().nextInt(100) < 15) {
                    System.out.println("⚠️  [ERRONEOUS BREAK] System auto-breaking " + user + " (missed calls).");
                    onErroneousBreak.set(true);

                    // Pull agent off the available queue immediately
                    availableAgents.removeIf(c -> c.userId.equals(user));

                    // user_readiness: fire erroneous break event
                    AgentActivityEvent errEvent = new AgentActivityEvent();
                    errEvent.setSessionId(sessionId);
                    errEvent.setCampaignId(baseActivityEvent.getCampaignId());
                    errEvent.setEventType("AGENT_SET_BREAK");
                    errEvent.setBreakEndTime(Instant.now());
                    errEvent.setBreakReason("System Initiated");
                    errEvent.setAgentBreakReason("erroneous.channel.system.initiated.break");
                    activityProducer.send(errEvent);

                    // Agent sits in erroneous break for 30–90 seconds
                    Thread.sleep(ThreadLocalRandom.current().nextLong(30000, 90000));

                    // If still on shift, return agent to the pool
                    if (activeAgents.contains(user)) {
                        onErroneousBreak.set(false);

                        AgentContext ctx = latestAgentContexts.get(user);
                        if (ctx != null) {
                            availableAgents.put(ctx);
                            System.out.println(" [ERRONEOUS BREAK OVER] " + user + " back in pool.");

                            // user_readiness: fire READY-resumed event
                            AgentActivityEvent resumeEvent = new AgentActivityEvent();
                            resumeEvent.setSessionId(sessionId);
                            resumeEvent.setCampaignId(baseActivityEvent.getCampaignId());
                            resumeEvent.setEventType("AGENT_SET_READY");
                            resumeEvent.setReadyStartTime(Instant.now());
                            resumeEvent.setBreakEndTime(Instant.now());
                            resumeEvent.setAgentBreakReason("Unavailable");
                            activityProducer.send(resumeEvent);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // THE CALL BRAIN — generates inbound traffic at random intervals
    private void generateInboundCalls(ExecutorService callPool) {
        try {
            while (true) {
                // A new caller dials in every 2–6 seconds
                Thread.sleep(ThreadLocalRandom.current().nextLong(2000, 6000));
                callPool.submit(this::handleSingleCall);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // SINGLE CALL HANDLER
    // Full lifecycle: IVR → Queue → Ring → Connect
    //               → [Hold] → [Conference] → [Transfer] → Disconnect
    //               → Wrap-up → Disposition → Agent back to pool
    //
    private void handleSingleCall() {
        try {
            Integer campaignId = CAMPAIGN_IDS.get(
                ThreadLocalRandom.current().nextInt(CAMPAIGN_IDS.size()));
            String stateName   = STATE_MAP.get(campaignId);

            String crtObjectId = "crt-"  + UUID.randomUUID().toString().substring(0, 8);
            String callId      = "call-" + UUID.randomUUID().toString().substring(0, 8);
            String callLegId   = "leg-"  + UUID.randomUUID().toString().substring(0, 8);
            Instant originateTime = Instant.now();

            System.out.println(" [INBOUND] Caller from " + stateName + " entered the system.");

            // ── IVR ─────────────────────────────────────────────
            // Caller listens to IVR menu and presses options (5–15 seconds).
            Instant ivrEntryTime = Instant.now();
            callProducer.send(buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                originateTime, "IVR_ENTERED", ivrEntryTime,
                null, null, null, null, null, null, null, null));

            long ivrDurationMs = ThreadLocalRandom.current().nextLong(5000, 15000);
            Thread.sleep(ivrDurationMs);

            // ── QUEUE ENTRY ──────────────────────────────────────
            // Caller enters the hold queue. queue_time begins here.
            Instant queueEntryTime = Instant.now();
            CallEvent queueEvent = buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                originateTime, "QUEUE_ENTERED", queueEntryTime,
                null, null, null, null, null, null, ivrDurationMs, null);
            callProducer.send(queueEvent);

            // cm_cdr_history: telephony leg opens when call hits the queue.
            // setup_time = internal SIP signalling lag before audio ports are allocated.
            Instant legStartTime = Instant.now();
            long setupTimeMs     = ThreadLocalRandom.current().nextLong(80, 600);

            CmCdrEvent cdrLeg = new CmCdrEvent();
            cdrLeg.setCallLegId(callLegId);
            cdrLeg.setStartTime(legStartTime);
            cdrLeg.setSetupTime(setupTimeMs);
            cdrLeg.setVoiceResourceInitializationTime(legStartTime.plusMillis(setupTimeMs));
            // End-of-leg fields will be filled in before the final cdrProducer.send()

            // ── PROVIDER FAILURE (3%) ───────────────────────────
            // Occasional telco/SIP failure before any agent is even involved.
            if (ThreadLocalRandom.current().nextInt(100) < 3) {
                System.out.println("🔌 [PROVIDER FAIL] Telco error for caller from " + stateName);
                Instant failTime = Instant.now();

                buildCallEvent( crtObjectId, callId, callLegId, null,   // 👈 campaignId is nullable
                originateTime,"DISCONNECTED", failTime,failTime, "PROVIDER_FAILURE",
                "FAILURE", false,
    0L, ivrDurationMs, null, "service.unavailable"
);

                cdrLeg.setEndTime(failTime);
                cdrLeg.setRingTime(0L);
                cdrLeg.setTalkTime(0L);
                cdrLeg.setHangupCause("service.unavailable");
                cdrLeg.setHangupCauseCode(503);
                cdrLeg.setWhichSideHungup("terminator");
                cdrLeg.setInternalHangupReason("PROVIDER_FAILURE: upstream SIP trunk rejected");
                cdrProducer.send(cdrLeg);
                return;
            }

            // ──  WAIT FOR AGENT (max 10 seconds) ─────────────────
            // Poll the available-agents queue. If nobody answers in 10s → abandoned.
            AgentContext primaryAgent = availableAgents.poll(10, TimeUnit.SECONDS);
            Instant queueExitTime = Instant.now();
            long queueWaitMs = ChronoUnit.MILLIS.between(queueEntryTime, queueExitTime);

            if (primaryAgent == null) {
                // ── ABANDONED ───────────────────────────────────────────
                // Caller hung up while waiting. hangup_on_hold = true.
                System.out.println("[ABANDONED] Caller from " + stateName
                    + " gave up after " + queueWaitMs + "ms.");
                Instant abandonTime = Instant.now();

                callProducer.send(buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                    originateTime, "DISCONNECTED", abandonTime, abandonTime,
                    "CALL_HANGUP", "FAILURE", true, 0L,
                    ivrDurationMs, null, "originator.cancel"));

                cdrLeg.setEndTime(abandonTime);
                cdrLeg.setRingTime(0L);
                cdrLeg.setTalkTime(0L);
                cdrLeg.setHangupCause("originator.cancel");
                cdrLeg.setHangupCauseCode(487);
                cdrLeg.setWhichSideHungup("caller");
                cdrProducer.send(cdrLeg);
                return;
            }

            // ──  RINGING ──────────────────────────────────────────
            // Phone rings at the agent's device. ringing_time measured.
            // TABLE: calls → RINGING
            System.out.println(" [RINGING] " + stateName + " caller → "
                + primaryAgent.userId + " (queue wait: " + queueWaitMs + "ms)");
            Instant ringStartTime = Instant.now();
            long ringDurationMs   = ThreadLocalRandom.current().nextLong(1000, 4000);

            callProducer.send(buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                originateTime, "RINGING", ringStartTime,
                null, null, null, null, null, null, null, null));

            Thread.sleep(ringDurationMs);

            // ── CONNECTED ────────────────────────────────────────
            // Agent picks up. Audio bridge established.
            Instant connectedTime = Instant.now();
            long talkTimeSec      = ThreadLocalRandom.current().nextLong(60, 300);

            System.out.println(" [CONNECTED] " + primaryAgent.userId + " answered (" + stateName + ").");
            callProducer.send(buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                originateTime, "CONNECTED", connectedTime,
                null, "CONNECTED", "SUCCESS", false, null,
                null, ringDurationMs, null));

            // ── CONFERENCE JOIN (10%) ────────────────────────────
            // A second agent is pulled into the call (e.g., supervisor escalation).
            // both agents accrue talk_time simultaneously — never sum these when computing total call duration for a crt_object_id.
            AgentContext conferenceAgent = null;
            if (ThreadLocalRandom.current().nextInt(100) < 10) {
                // Non-blocking poll: only conference if an agent is immediately free
                conferenceAgent = availableAgents.poll(0, TimeUnit.SECONDS);
                if (conferenceAgent != null) {
                    System.out.println(" [CONFERENCE] " + conferenceAgent.userId
                        + " joined call with " + primaryAgent.userId);
                    callProducer.send(buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                        originateTime, "CONFERENCE_JOINED", Instant.now(),
                        null, null, null, null, null, null, null, null));
                }
            }

            // ── HOLD (30%) ──────────────────────────────────────
            // Agent places caller on hold to consult notes / transfer / escalate.
            // hold_time and customer_hold_time are tracked separately.
            long totalHoldTimeMs = 0;
            if (ThreadLocalRandom.current().nextInt(100) < 30) {
                long holdDurationMs = ThreadLocalRandom.current().nextLong(5000, 20000);
                totalHoldTimeMs = holdDurationMs;
                System.out.println(" [HOLD] " + primaryAgent.userId
                    + " placed caller on hold (" + holdDurationMs + "ms).");

                callProducer.send(buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                    originateTime, "HOLD_STARTED", Instant.now(),
                    null, null, null, null, null, null, null, null));

                Thread.sleep(holdDurationMs);

                callProducer.send(buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                    originateTime, "HOLD_ENDED", Instant.now(),
                    null, null, null, null, null, null, null, null));
                System.out.println(" [RESUMED] " + primaryAgent.userId + " resumed call.");
            }

            // Brief simulated conversation
            Thread.sleep(5000);

            // ──  TRANSFER (20%) ───────────────────────────────────
            // Agent transfers caller to the Feedback IVR (campaign id = 4).
            // This creates a NEW call_id but keeps the same crt_object_id.
            // callType changes to transferred.to.campaign.detail.
            boolean isTransferred = ThreadLocalRandom.current().nextInt(100) < 20;
            Instant transferTime  = null;
            if (isTransferred) {
                transferTime = Instant.now();
                String transferCallId  = "call-" + UUID.randomUUID().toString().substring(0, 8);
                String transferLegId   = "leg-"  + UUID.randomUUID().toString().substring(0, 8);

                System.out.println("[TRANSFER] " + primaryAgent.userId
                    + " transferring to Feedback IVR (campaign 4).");

                // New call record under the same crt_object_id, new call_id
                CallEvent transferEvent = buildCallEvent(crtObjectId, transferCallId, transferLegId,
                    4, originateTime, "TRANSFERRED", transferTime,
                    null, null, null, null, null, null, null, null);
                transferEvent.setCallType("transferred.to.campaign.detail");
                callProducer.send(transferEvent);
            }

            // ── DISCONNECTED ────────────────────────────────────
            // Call ends. All timing fields are now final.
            Instant disconnectedTime = Instant.now();
            long talkTimeMs          = talkTimeSec * 1000L;
            boolean callerHungUp     = ThreadLocalRandom.current().nextInt(100) < 60;

            System.out.println("🔚 [HANGUP] " + primaryAgent.userId
                + " | talk=" + talkTimeSec + "s  hold=" + totalHoldTimeMs/1000
                + "s  queue=" + queueWaitMs + "ms");

            callProducer.send(buildCallEvent(crtObjectId, callId, callLegId, campaignId,
                originateTime, "DISCONNECTED", disconnectedTime, disconnectedTime,
                "CALL_HANGUP", "SUCCESS", false, talkTimeMs,
                ivrDurationMs, ringDurationMs, "normal.clearing"));
   
            // cm_cdr_history: finalise the leg record
            cdrLeg.setEndTime(disconnectedTime);
            cdrLeg.setRingTime(ringDurationMs);
            cdrLeg.setTalkTime(talkTimeMs);
            cdrLeg.setHangupCause("normal.clearing");
            cdrLeg.setHangupCauseCode(16); // Q.850 normal call clearing
            cdrLeg.setWhichSideHungup(callerHungUp ? "caller" : "terminator");
            cdrLeg.setInternalHangupReason(null);
            cdrProducer.send(cdrLeg);

            // ── WRAP-UP & DISPOSITION — PRIMARY AGENT ──────────
            // Agent is NOT returned to the pool until wrap-up is complete.
            // They enter the call outcome and save it (or the CRM auto-saves).
            long wrapTimeMs = ThreadLocalRandom.current().nextLong(15000, 60000);
            System.out.println("📝 [WRAP UP] " + primaryAgent.userId
                + " entering disposition (" + wrapTimeMs / 1000 + "s)...");
            Thread.sleep(wrapTimeMs);

            String dispClass = pickRandom(DISPOSITION_CLASSES);
            String dispCode  = pickRandom(DISPOSITION_MAP.get(dispClass));

            sendDispositionEvent(
                callLegId, callId, campaignId,
                primaryAgent,
                connectedTime, disconnectedTime,
                talkTimeMs, totalHoldTimeMs, wrapTimeMs,
                transferTime, isTransferred ? "4" : null,
                dispClass, dispCode,
                "normal"   // 1-on-1 call from primary agent's perspective
            );
            System.out.println("  [DISPOSED] " + primaryAgent.userId
                + " → " + dispClass + ": " + dispCode);

            // ──  WRAP-UP & DISPOSITION — CONFERENCE AGENT ────────
            // Conference agent gets its OWN disposition row with associationType=conference.
                       if (conferenceAgent != null) {
                long confWrapMs  = ThreadLocalRandom.current().nextLong(10000, 30000);
                Thread.sleep(confWrapMs);

                String confClass = pickRandom(DISPOSITION_CLASSES);
                String confCode  = pickRandom(DISPOSITION_MAP.get(confClass));

                sendDispositionEvent(
                    callLegId, callId, campaignId,
                    conferenceAgent,
                    connectedTime, disconnectedTime,
                    talkTimeMs, 0L, confWrapMs,
                    null, null,
                    confClass, confCode,
                    "conference"  // marks overlapping talk time — do not aggregate
                );
                System.out.println(" [CONF DISPOSED] " + conferenceAgent.userId
                    + " → " + confClass + ": " + confCode);

                // Return conference agent to the pool if still on shift
                if (activeAgents.contains(conferenceAgent.userId)) {
                    availableAgents.put(conferenceAgent);
                    System.out.println("  [CONF READY] " + conferenceAgent.userId + " back in pool.");
                }
            }

            // ──  PRIMARY AGENT BACK IN POOL ──────────────────────
            // Only re-queue if: still on shift AND not in an erroneous break
            AtomicBoolean onBreak = erroneousBreakFlags.get(primaryAgent.userId);
            if (activeAgents.contains(primaryAgent.userId)
                    && (onBreak == null || !onBreak.get())) {
                availableAgents.put(primaryAgent);
                System.out.println("  [READY] " + primaryAgent.userId + " back in pool.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // HELPER — Build a DispositionEvent for user_disposition_history
    private void sendDispositionEvent(
            String callLegId,     String callId,        Integer campaignId,
            AgentContext agent,
            Instant connectedTime, Instant disconnectedTime,
            long talkTimeMs,      long holdTimeMs,      long wrapTimeMs,
            Instant transferTime,  String transferTo,
            String dispClass,      String dispCode,
            String associationType) {

        UserDisposition d = new UserDisposition();
        d.setId("disp-" + UUID.randomUUID().toString().substring(0, 8));

        // Identity / linking
        d.setCallLegId(callLegId);
        d.setCallId(callId);
        d.setCampaignId(campaignId);
        d.setUserId(agent.userId);
        d.setSessionId(agent.sessionId);

        // Timestamps
        d.setDateAdded(connectedTime);                  // when the call rang this agent
        d.setUserConnectedTime(connectedTime);           // when agent audio joined bridge
        d.setUserDisconnectedTime(disconnectedTime);     // when agent audio left bridge
        d.setUserDispositionTime(Instant.now());         // when agent clicked Save
        d.setAutoCallOnTime(agent.autoCallOnTime);       // links back to auto_call table

        // Transfer fields (null when not transferred)
        d.setTransferTime(transferTime);
        d.setTransferTo(transferTo);

        // Metrics
        d.setTalkTime(talkTimeMs);                      // this agent's audio duration
        d.setHoldTime(holdTimeMs);                      // total agent-side hold
        d.setCustomerHoldTime(holdTimeMs);              // what the caller experienced
        d.setWrapTime(wrapTimeMs);                      // after-call work duration

        // Outcome
        d.setDispositionClass(dispClass);
        d.setDispositionCode(dispCode);
        d.setAssociationType(associationType);          // "normal" or "conference"

        // Flags
        d.setWorking(false);                            // no longer active after dispose
        d.setDisposedByCrm(ThreadLocalRandom.current().nextInt(100) < 10); // 10% auto-closed

        dispositionProducer.send(d);
    }

    // HELPER — Build a CallEvent with the most common fields populated.
    private CallEvent buildCallEvent(
            String  crtObjectId,   String  callId,         String  callLegId,
            Integer campaignId,    Instant originateTime,  String  eventType,
            Instant eventTimestamp,
            Instant callEndTime,   String  systemDisp,     String  callResult,
            Boolean hangupOnHold,  Long    talkTime,
            Long    ivrTime,        Long    ringingTime,    String  hangupCause) {

        CallEvent e = new CallEvent();
        e.setCrtObjectId(crtObjectId);
        e.setCallId(callId);
        e.setCallLegId(callLegId);
        e.setCampaignId(campaignId);
        e.setCallOriginateTime(originateTime);
        e.setIsOutbound(false);
        e.setCallType("inbound.call.dial");
        e.setEventType(eventType);
        if (eventTimestamp != null) e.setEventTimestamp(eventTimestamp);
        if (callEndTime    != null) e.setCallEndTime(callEndTime);
        if (systemDisp     != null) e.setSystemDisposition(systemDisp);
        if (callResult     != null) e.setCallResult(callResult);
        if (hangupOnHold   != null) e.setHangupOnHold(hangupOnHold);
        if (talkTime       != null) e.setTalkTime(talkTime);
        if (ivrTime        != null) e.setIvrTime(ivrTime);
        if (ringingTime    != null) e.setRingingTime(ringingTime);
        if (hangupCause    != null) e.setHangupCause(hangupCause);
        return e;
    }

    // HELPER — Base CallEvent 
    private CallEvent createBaseCallEvent(
            String crt, String callId, String legId, Integer campaign, Instant originate) {
        CallEvent e = new CallEvent();
        e.setCrtObjectId(crt);
        e.setCallId(callId);
        e.setCallLegId(legId);
        e.setCampaignId(campaign);
        e.setIsOutbound(false);
        e.setCallType("inbound.call.dial");
        e.setCallOriginateTime(originate);
        return e;
    }

    // HELPER — Fire a user_sessions event
    private void sendSessionEvent(String user, String sessionId, String type, String reason) {
        UserSessionEvent e = new UserSessionEvent();
        e.setSessionId(sessionId);
        e.setUserId(user);
        e.setEventType(type);
        e.setTimestamp(Instant.now());
        e.setReason(reason);
        sessionProducer.send(e);
    }

    // HELPER — Pick a random element from a list
    private <T> T pickRandom(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}