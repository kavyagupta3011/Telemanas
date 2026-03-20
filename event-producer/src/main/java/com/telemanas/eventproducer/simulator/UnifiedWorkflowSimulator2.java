package com.telemanas.eventproducer.simulator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.telemanas.eventproducer.model.AgentActivityEvent;
import com.telemanas.eventproducer.model.AutoCallEvent;
import com.telemanas.eventproducer.model.CallEvent;
import com.telemanas.eventproducer.model.UserSessionEvent;
import com.telemanas.eventproducer.service.AgentActivityProducerService;
import com.telemanas.eventproducer.service.AutoCallProducerService;
import com.telemanas.eventproducer.service.CallProducerService;
import com.telemanas.eventproducer.service.UserSessionProducerService;

@Component
public class UnifiedWorkflowSimulator2 implements CommandLineRunner {

    private final UserSessionProducerService sessionProducer;
    private final AgentActivityProducerService activityProducer;
    private final AutoCallProducerService autoCallProducer;
    private final CallProducerService callProducer; 
    private final Random random = new Random();

    // --- REAL-TIME CALL CENTER STATE ---
    // Tracks agents who are logged in right now
    private final Set<String> activeAgents = ConcurrentHashMap.newKeySet(); 
    // The "Hold Queue" - Agents waiting for a call
    private final BlockingQueue<String> availableAgents = new LinkedBlockingQueue<>(); 

    // The Official Tele-MANAS Campaign to State Mapping
    private static final Map<Integer, String> STATE_MAP = Map.ofEntries(
        Map.entry(3, "Andhra Pradesh"), Map.entry(10, "Chandigarh"), Map.entry(11, "Chhattisgarh"),
        Map.entry(12, "Ladakh"), Map.entry(13, "Manipur"), Map.entry(14, "Mizoram"),
        Map.entry(15, "Odisha"), Map.entry(16, "Pondicherry"), Map.entry(17, "Punjab"),
        Map.entry(18, "Rajasthan"), Map.entry(19, "Uttarakhand"), Map.entry(20, "Delhi"),
        Map.entry(21, "Uttar Pradesh"), Map.entry(22, "Haryana"), Map.entry(23, "Telangana"),
        Map.entry(24, "Assam"), Map.entry(25, "Bihar"), Map.entry(26, "Jharkhand"),
        Map.entry(27, "Tamil Nadu"), Map.entry(28, "Gujarat"), Map.entry(29, "Dadra & Daman & Diu"),
        Map.entry(30, "Himachal Pradesh"), Map.entry(31, "Jammu & Kashmir"), Map.entry(32, "Maharashtra"),
        Map.entry(33, "Kerala"), Map.entry(34, "Lakshadweep"), Map.entry(35, "Karnataka"),
        Map.entry(36, "Andaman & Nicobar"), Map.entry(37, "Goa"), Map.entry(38, "West Bengal"),
        Map.entry(39, "Madhya Pradesh"), Map.entry(40, "Sikkim"), Map.entry(41, "Arunachal Pradesh"),
        Map.entry(42, "Tripura"), Map.entry(43, "Meghalaya"), Map.entry(44, "Nagaland"),
        Map.entry(47, "AFMS")
    );

    public UnifiedWorkflowSimulator2(
            UserSessionProducerService sessionProducer,
            AgentActivityProducerService activityProducer,
            AutoCallProducerService autoCallProducer,
            CallProducerService callProducer) {
        this.sessionProducer = sessionProducer;
        this.activityProducer = activityProducer;
        this.autoCallProducer = autoCallProducer;
        this.callProducer = callProducer;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting TRUE ASYNC Call Center Simulation...");
        List<String> users = List.of("user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8", "user9", "user10", "user11", "user12", "user13", "user14", "user15", "user16", "user17", "user18", "user19", "user20");

        ExecutorService agentPool = Executors.newFixedThreadPool(users.size());
        ExecutorService callPool = Executors.newCachedThreadPool();

        // 1. Boot up the Agent Brains (Independent Shifts)
        for (String user : users) {
            agentPool.submit(() -> runAgentShift(user));
        }

        // 2. Boot up the Call Brain (Independent Inbound Traffic)
        new Thread(() -> generateInboundCalls(callPool)).start();
    }

    // ==========================================
    // THE AGENT BRAIN
    // ==========================================
    private void runAgentShift(String user) {
        try {
            while (true) {
                // Stagger login times
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 15000));

                String sessionId = "sess-" + UUID.randomUUID().toString().substring(0, 8);
                
                // 1. Agent Logs In
                sendSessionEvent(user, sessionId, "LOGIN", null);
                activeAgents.add(user);
                System.out.println("🟢 [AGENT] " + user + " logged in.");

                // 2. Delay before Agent goes READY
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5000));
                
                //  FIRE READY ON EVENT
                AgentActivityEvent activityEvent = new AgentActivityEvent();
                activityEvent.setSessionId(sessionId);
                activityEvent.setCampaignId(101); // Default simulation campaign
                activityEvent.setReadyStartTime(Instant.now());
                activityEvent.setEventType("AGENT_SET_READY");
                activityEvent.setBreakEndTime(Instant.now());
                activityEvent.setSessionId(sessionId);
                activityProducer.send(activityEvent);
                System.out.println("🟡 [AGENT] " + user + " is now READY.");

                // 3. Delay before AutoCall turns ON
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5000));
                
                //  FIRE AUTOCALL ON EVENT
                AutoCallEvent autoCallEvent = new AutoCallEvent();
                autoCallEvent.setId("ac-" + UUID.randomUUID().toString().substring(0, 8));
                autoCallEvent.setSessionId(sessionId);
                autoCallEvent.setCampaignId(101); 
                autoCallEvent.setAutoCallStartReason("System Trigger");
                autoCallEvent.setAutoCallOnStartTime(Instant.now());
                autoCallProducer.send(autoCallEvent);
                System.out.println("🔵 [AGENT] " + user + " AutoCall is ON.");

                // ONLY NOW is the agent allowed to pick up calls
                availableAgents.put(user); 

                // 4. Agent stays on shift for 1 to 3 minutes
                Thread.sleep(ThreadLocalRandom.current().nextInt(60000, 180000));

                // 5. Agent Shift Ends - Turn everything OFF
                activeAgents.remove(user);
                availableAgents.remove(user); // Pull them out of the queue
                Instant endTime = Instant.now();

                // 🔥 FIRE AUTOCALL OFF EVENT (Updates the existing DB row)
                autoCallEvent.setAutoCallOnEndTime(endTime);
                autoCallEvent.setAutoCallOffEndTime(endTime.plusSeconds(2));
                autoCallEvent.setEndReason("Batch Completed");
                autoCallProducer.send(autoCallEvent);
                System.out.println("📴 [AGENT] " + user + " AutoCall turned OFF.");

                //  FIRE READY OFF EVENT (Updates the existing DB row)
                activityEvent.setReadyEndTime(endTime.plusSeconds(2));
                // Create a list of possible activity end/break reasons
                List<String> breakReasons = List.of(
                    "Shift Ended", "Shift Ended", "Shift Ended", // Weighted to be most common
                    "Lunch Break", "Tea Break", "Meeting", 
                    "Training", "System Issue"
                );
                
                // Pick a random reason from the list
                String randomBreakReason = breakReasons.get(ThreadLocalRandom.current().nextInt(breakReasons.size()));
                
                // Apply it to the event
                activityEvent.setBreakReason(randomBreakReason);
                activityProducer.send(activityEvent);
                System.out.println("🛑 [AGENT] " + user + " is no longer READY.");

                // 6. Agent Logs Out
                List<String> reasons = List.of(
                    "End of Shift", "End of Shift", "End of Shift", 
                    "Lunch Break", "Tea Break", "Technical Issue"
                );
                String randomReason = reasons.get(ThreadLocalRandom.current().nextInt(reasons.size()));

                sendSessionEvent(user, sessionId, "LOGOUT", randomReason);
                System.out.println("🔴 [AGENT] " + user + " logged out (" + randomReason + ").");

                // Agent takes a break before their next shift
                Thread.sleep(ThreadLocalRandom.current().nextInt(30000, 60000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==========================================
    // THE CALL BRAIN
    // ==========================================
    private void generateInboundCalls(ExecutorService callPool) {
        try {
            while (true) {
                // A new caller dials in every 2 to 6 seconds
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 6000));
                callPool.submit(this::handleSingleCall);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleSingleCall() {
        try {
            // Setup Call Data
            List<Integer> keys = new ArrayList<>(STATE_MAP.keySet());
            Integer campaignId = keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
            String stateName = STATE_MAP.get(campaignId); 

            String crtObjectId = "crt-" + UUID.randomUUID().toString().substring(0, 8);
            String callId = "call-" + UUID.randomUUID().toString().substring(0, 8);
            String callLegId = "leg-" + UUID.randomUUID().toString().substring(0, 8);
            Instant originateTime = Instant.now();

            System.out.println("☎️ [CALL IN] Citizen from " + stateName + " is in the IVR...");
            
            // 1. IVR ENTERED
            CallEvent ivrEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, originateTime);
            ivrEvent.setEventType("IVR_ENTERED");
            ivrEvent.setEventTimestamp(originateTime);
            callProducer.send(ivrEvent);

            Thread.sleep(3000); // Caller spends 3 seconds in IVR

            // 2. QUEUE ROUTING (The Magic Happens Here)
            // The call waits up to 10 seconds for an agent to become available
            String assignedAgent = availableAgents.poll(10, TimeUnit.SECONDS);

            if (assignedAgent == null) {
                // ABANDONED CALL! Nobody answered in time.
                System.out.println("❌ [ABANDONED] Citizen from " + stateName + " hung up. Wait time exceeded.");
                CallEvent abandonEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, originateTime);
                abandonEvent.setEventType("DISCONNECTED");
                abandonEvent.setEventTimestamp(Instant.now());
                abandonEvent.setCallEndTime(Instant.now());
                abandonEvent.setSystemDisposition("CALL_HANGUP");
                abandonEvent.setCallResult("FAILURE");
                abandonEvent.setHangupOnHold(true); // They hung up while waiting!
                abandonEvent.setTalkTime(0L);
                callProducer.send(abandonEvent);
                return; // End the call flow
            }

            // 3. RINGING
            System.out.println("🔔 [ROUTING] Call sent to " + assignedAgent + "...");
            CallEvent ringingEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, originateTime);
            ringingEvent.setEventType("RINGING");
            ringingEvent.setEventTimestamp(Instant.now());
            callProducer.send(ringingEvent);
            Thread.sleep(2000); // Rings for 2 seconds

            // 4. CONNECTED
            long simulatedTalkTime = ThreadLocalRandom.current().nextInt(60, 300); // 1 to 5 mins
            System.out.println("✅ [CONNECTED] " + assignedAgent + " answered. Talking for " + simulatedTalkTime + "s.");
            CallEvent connectEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, originateTime);
            connectEvent.setEventType("CONNECTED");
            connectEvent.setEventTimestamp(Instant.now());
            connectEvent.setSystemDisposition("CONNECTED");
            connectEvent.setCallResult("SUCCESS");
            callProducer.send(connectEvent);

            // Sleep thread briefly to simulate conversation physically occurring
            Thread.sleep(5000); 

            // 5. DISCONNECTED
            CallEvent disconnectEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, originateTime);
            disconnectEvent.setEventType("DISCONNECTED");
            disconnectEvent.setEventTimestamp(Instant.now());
            disconnectEvent.setCallEndTime(Instant.now());
            disconnectEvent.setSystemDisposition("CALL_HANGUP");
            disconnectEvent.setHangupOnHold(false);
            disconnectEvent.setTalkTime(simulatedTalkTime * 1000);
            callProducer.send(disconnectEvent);
            System.out.println("🔚 [HANGUP] " + assignedAgent + " finished the call.");

            // Put the agent BACK into the available pool for the next caller (if they didn't log out)
            if (activeAgents.contains(assignedAgent)) {
                availableAgents.put(assignedAgent);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // --- HELPER METHODS ---
    private CallEvent createBaseCallEvent(String crt, String callId, String legId, Integer campaign, Instant originate) {
        CallEvent event = new CallEvent();
        event.setCrtObjectId(crt);
        event.setCallId(callId);
        event.setCallLegId(legId);
        event.setCampaignId(campaign);
        event.setIsOutbound(false);
        event.setCallType("inbound.call.dial");
        event.setCallOriginateTime(originate);
        return event;
    }

    private void sendSessionEvent(String user, String sessionId, String type, String reason) {
        UserSessionEvent event = new UserSessionEvent();
        event.setSessionId(sessionId);
        event.setUserId(user);
        event.setEventType(type);
        event.setTimestamp(Instant.now());
        event.setReason(reason);
        sessionProducer.send(event);
    }
}