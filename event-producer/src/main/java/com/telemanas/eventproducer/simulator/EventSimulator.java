package com.telemanas.eventproducer.simulator;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.telemanas.eventproducer.model.Event;


public class EventSimulator {

    private static final String API_URL = "http://localhost:8081/api/events";
    private static final Random random = new Random();

    private static final List<String> STATES = List.of("Karnataka", "Maharashtra", "Delhi");
    private static final Map<String, List<String>> CITIES = Map.of(
            "Karnataka", List.of("Bengaluru", "Mysuru"),
            "Maharashtra", List.of("Mumbai", "Pune"),
            "Delhi", List.of("New Delhi")
    );

    private static final List<String> AGE_GROUPS = List.of("<18", "18-30", "31-50", "50+");
    private static final List<String> GENDERS = List.of("M", "F", "O");

    private static final Set<String> loggedInUsers = new HashSet<>();
    private static final Set<String> usersOnCall = new HashSet<>();

    // a Spring tool used to make HTTP requests
    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) throws InterruptedException {

        List<String> users = List.of("user-1", "user-2", "user-3", "user-4");

        System.out.println("Starting Tele-MANAS Event Simulation...");

        while (true) {
            String user = users.get(random.nextInt(users.size()));

            if (!loggedInUsers.contains(user)) {
                login(user);
            } else if (!usersOnCall.contains(user) && random.nextBoolean()) {
                startCall(user);
            } else if (usersOnCall.contains(user)) {
                endCall(user);
            } else {
                logout(user);
            }

            Thread.sleep(1000 + random.nextInt(4000)); // 1–5 sec gap
        }
    }

    private static void login(String user) {
        loggedInUsers.add(user);
        sendEvent("USER_LOGIN", user);
    }

    private static void logout(String user) {
        if (!usersOnCall.contains(user)) {
            loggedInUsers.remove(user);
            sendEvent("USER_LOGOUT", user);
        }
    }

    private static void startCall(String user) {
        usersOnCall.add(user);
        sendEvent("CALL_STARTED", user);
    }

    private static void endCall(String user) {
        usersOnCall.remove(user);
        sendEvent("CALL_ENDED", user);
    }

    private static void sendEvent(String type, String user) {
        String state = STATES.get(random.nextInt(STATES.size()));
        String city = CITIES.get(state).get(0);

        Event event = new Event();
        event.setEventType(type);
        event.setUserId(user);
        event.setTimestamp(Instant.now());
        event.setState(state);
        event.setCity(city);
        event.setEmergency(random.nextBoolean());
        event.setAgeGroup(AGE_GROUPS.get(random.nextInt(AGE_GROUPS.size())));
        event.setGender(GENDERS.get(random.nextInt(GENDERS.size())));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Event> request = new HttpEntity<>(event, headers);
        restTemplate.postForEntity(API_URL, request, String.class);

        System.out.println("Sent: " + type + " for " + user);
    }
}
