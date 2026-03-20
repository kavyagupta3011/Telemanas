package com.telemanas.eventproducer.simulator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.telemanas.eventproducer.model.CallEvent;
import com.telemanas.eventproducer.model.UserSessionEvent;
import com.telemanas.eventproducer.service.AgentActivityProducerService;
import com.telemanas.eventproducer.service.AutoCallProducerService;
import com.telemanas.eventproducer.service.CallProducerService;
import com.telemanas.eventproducer.service.UserSessionProducerService;

@Component
public class UnifiedWorkflowSimulator implements CommandLineRunner {

    private final UserSessionProducerService sessionProducer;
    private final AgentActivityProducerService activityProducer;
    private final AutoCallProducerService autoCallProducer;
    private final CallProducerService callProducer; 
    private final Random random = new Random();

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

    public UnifiedWorkflowSimulator(
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
        System.out.println("Starting CONCURRENT Live Call Center Simulation...");
        List<String> users = List.of("user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8", "user9", "user10", "user11", "user12", "user13", "user14", "user15", "user16", "user17", "user18", "user19", "user20");

        // Create a Thread Pool with enough capacity for all your agents
        ExecutorService executor = Executors.newFixedThreadPool(users.size());

        for (String user : users) {
            // Submit an infinite loop task for EACH agent to run simultaneously
            executor.submit(() -> {
                try {
                    // Stagger the logins by 0-10 seconds so they don't all hit Kafka at the exact same millisecond
                    Thread.sleep(ThreadLocalRandom.current().nextInt(10000));

                    // Each agent gets their own timeline tracker
                    Instant baseTime = Instant.now().minus(24, ChronoUnit.HOURS);

                    while (true) {
                        // --- 1. SETUP TIMELINES & IDs ---
                        String sessionId = "sess-" + UUID.randomUUID().toString().substring(0, 8);
                        
                        List<Integer> campaignKeys = new ArrayList<>(STATE_MAP.keySet());
                        Integer campaignId = campaignKeys.get(ThreadLocalRandom.current().nextInt(campaignKeys.size()));
                        String stateName = STATE_MAP.get(campaignId); 

                        String crtObjectId = "crt-" + UUID.randomUUID().toString().substring(0, 8);
                        String callId = "call-" + UUID.randomUUID().toString().substring(0, 8);
                        String callLegId = "leg-" + UUID.randomUUID().toString().substring(0, 8);

                        Instant loginTime = baseTime.plus(ThreadLocalRandom.current().nextInt(60), ChronoUnit.MINUTES);
                        Instant readyStartTime = loginTime.plusSeconds(60); 
                        
                        Instant callOriginateTime = readyStartTime.plusSeconds(30);
                        Instant ringingTimeStart = callOriginateTime.plusSeconds(15); 
                        Instant connectTime = ringingTimeStart.plusSeconds(5);        
                        
                        long talkDurationSeconds = 120 + ThreadLocalRandom.current().nextInt(300);         
                        Instant disconnectTime = connectTime.plusSeconds(talkDurationSeconds);
                        
                        Instant logoutTime = disconnectTime.plusSeconds(60);
                        baseTime = logoutTime.plus(2, ChronoUnit.HOURS); 

                        // --- 2. FIRE EVENTS IN CHRONOLOGICAL REAL-TIME ---

                        // A. Agent Logs In
                        UserSessionEvent loginEvent = new UserSessionEvent();
                        loginEvent.setSessionId(sessionId);
                        loginEvent.setUserId(user);
                        loginEvent.setEventType("LOGIN");
                        loginEvent.setTimestamp(loginTime);
                        sessionProducer.send(loginEvent);
                        System.out.println("[" + user + "] Logged In");
                        Thread.sleep(1000); // Wait 1 sec

                        // B. Citizen Dials In (IVR)
                        CallEvent ivrEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, callOriginateTime);
                        ivrEvent.setEventType("IVR_ENTERED");
                        ivrEvent.setEventTimestamp(callOriginateTime);
                        callProducer.send(ivrEvent);
                        System.out.println("[" + user + "] --- Citizen from " + stateName + " entered IVR...");
                        Thread.sleep(1000); // Wait 1 sec

                        // C. Call Rings Agent
                        CallEvent ringingEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, callOriginateTime);
                        ringingEvent.setEventType("RINGING");
                        ringingEvent.setEventTimestamp(ringingTimeStart);
                        callProducer.send(ringingEvent);
                        System.out.println("[" + user + "] --- Ringing...");
                        Thread.sleep(1000); // Wait 1 sec

                        // D. Agent Picks Up
                        CallEvent connectEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, callOriginateTime);
                        connectEvent.setEventType("CONNECTED");
                        connectEvent.setEventTimestamp(connectTime);
                        connectEvent.setSystemDisposition("CONNECTED");
                        connectEvent.setCallResult("SUCCESS");
                        callProducer.send(connectEvent);
                        System.out.println("[" + user + "] --- Connected! Talking for " + talkDurationSeconds + " seconds.");
                        
                        // We scale down the talk time simulation so you don't actually wait 5 real minutes. 
                        // It waits 3 seconds in real life, but represents minutes in the database.
                        Thread.sleep(3000); 

                        // E. Citizen Hangs Up
                        CallEvent disconnectEvent = createBaseCallEvent(crtObjectId, callId, callLegId, campaignId, callOriginateTime);
                        disconnectEvent.setEventType("DISCONNECTED");
                        disconnectEvent.setEventTimestamp(disconnectTime);
                        disconnectEvent.setCallEndTime(disconnectTime);
                        disconnectEvent.setSystemDisposition("CALL_HANGUP");
                        disconnectEvent.setHangupCauseDescription("Normal Clearing");
                        disconnectEvent.setHangupOnHold(false);
                        disconnectEvent.setIvrTime(15L);
                        disconnectEvent.setRingingTime(5L);
                        disconnectEvent.setTalkTime(talkDurationSeconds * 1000); 
                        callProducer.send(disconnectEvent);
                        System.out.println("[" + user + "] --- Disconnected.");
                        Thread.sleep(1000); // Wait 1 sec

                        // F. Agent Logs Out
                        UserSessionEvent logoutEvent = new UserSessionEvent();
                        logoutEvent.setSessionId(sessionId);
                        logoutEvent.setUserId(user);
                        logoutEvent.setEventType("LOGOUT");
                        logoutEvent.setTimestamp(logoutTime);
                        logoutEvent.setReason("End of Shift");
                        sessionProducer.send(logoutEvent);
                        System.out.println("[" + user + "] Logged Out\n");

                        // Agent takes a short break before their next "shift" starts
                        Thread.sleep(4000); 
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                } catch (Exception e) {
                    System.err.println("Error in agent thread for " + user + ": " + e.getMessage());
                }
            });
        }
    }
    // Helper method to keep code clean
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
}