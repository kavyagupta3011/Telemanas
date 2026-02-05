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

    private static final List<String> STATES = List.of(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", 
        "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", 
        "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", 
        "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal",
        "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu", 
        "Delhi", "Jammu and Kashmir", "Ladakh", "Lakshadweep", "Puducherry"
    );

    
    private static final Map<String, List<String>> CITIES = Map.ofEntries(
        Map.entry("Andhra Pradesh", List.of("Visakhapatnam", "Vijayawada", "Guntur", "Nellore")),
        Map.entry("Arunachal Pradesh", List.of("Itanagar", "Tawang", "Pasighat")),
        Map.entry("Assam", List.of("Guwahati", "Silchar", "Dibrugarh", "Jorhat")),
        Map.entry("Bihar", List.of("Patna", "Gaya", "Bhagalpur", "Muzaffarpur")),
        Map.entry("Chhattisgarh", List.of("Raipur", "Bhilai", "Bilaspur", "Korba")),
        Map.entry("Goa", List.of("Panaji", "Margao", "Vasco da Gama")),
        Map.entry("Gujarat", List.of("Ahmedabad", "Surat", "Vadodara", "Rajkot")),
        Map.entry("Haryana", List.of("Gurugram", "Faridabad", "Panipat", "Ambala")),
        Map.entry("Himachal Pradesh", List.of("Shimla", "Manali", "Dharamshala", "Solan")),
        Map.entry("Jharkhand", List.of("Ranchi", "Jamshedpur", "Dhanbad", "Bokaro")),
        Map.entry("Karnataka", List.of("Bengaluru", "Mysuru", "Hubballi", "Mangaluru")),
        Map.entry("Kerala", List.of("Thiruvananthapuram", "Kochi", "Kozhikode", "Thrissur")),
        Map.entry("Madhya Pradesh", List.of("Bhopal", "Indore", "Gwalior", "Jabalpur")),
        Map.entry("Maharashtra", List.of("Mumbai", "Pune", "Nagpur", "Nashik")),
        Map.entry("Manipur", List.of("Imphal", "Thoubal", "Bishnupur")),
        Map.entry("Meghalaya", List.of("Shillong", "Tura", "Jowai")),
        Map.entry("Mizoram", List.of("Aizawl", "Lunglei", "Champhai")),
        Map.entry("Nagaland", List.of("Kohima", "Dimapur", "Mokokchung")),
        Map.entry("Odisha", List.of("Bhubaneswar", "Cuttack", "Rourkela", "Puri")),
        Map.entry("Punjab", List.of("Ludhiana", "Amritsar", "Jalandhar", "Chandigarh")),
        Map.entry("Rajasthan", List.of("Jaipur", "Jodhpur", "Udaipur", "Kota")),
        Map.entry("Sikkim", List.of("Gangtok", "Namchi", "Geyzing")),
        Map.entry("Tamil Nadu", List.of("Chennai", "Coimbatore", "Madurai", "Salem")),
        Map.entry("Telangana", List.of("Hyderabad", "Warangal", "Nizamabad", "Karimnagar")),
        Map.entry("Tripura", List.of("Agartala", "Udaipur", "Dharmanagar")),
        Map.entry("Uttar Pradesh", List.of("Lucknow", "Kanpur", "Ghaziabad", "Agra", "Varanasi")),
        Map.entry("Uttarakhand", List.of("Dehradun", "Haridwar", "Roorkee", "Nainital")),
        Map.entry("West Bengal", List.of("Kolkata", "Howrah", "Siliguri", "Durgapur")),
        Map.entry("Andaman and Nicobar Islands", List.of("Port Blair")),
        Map.entry("Chandigarh", List.of("Chandigarh")),
        Map.entry("Dadra and Nagar Haveli and Daman and Diu", List.of("Daman", "Silvassa")),
        Map.entry("Delhi", List.of("New Delhi", "Dwarka", "Saket")),
        Map.entry("Jammu and Kashmir", List.of("Srinagar", "Jammu", "Anantnag")),
        Map.entry("Ladakh", List.of("Leh", "Kargil")),
        Map.entry("Lakshadweep", List.of("Kavaratti")),
        Map.entry("Puducherry", List.of("Puducherry", "Karaikal"))
    );

    private static final List<String> AGE_GROUPS = List.of(
        "0-12", 
        "13-17", 
        "18-24", 
        "25-34", 
        "35-44", 
        "45-59", 
        "60-74", 
        "75+"
    );
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
