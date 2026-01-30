package com.telemanas.eventproducer.model;
import java.time.Instant;

public class Event {

    private String eventType; //USER_LOGIN, CALL_STARTED, CALL_ENDED, USER_LOGOUT
    private String userId;
    private Instant timestamp;
    private String state;
    private String city;
    private boolean isEmergency;
    private String ageGroup;
    private String gender;

    public String getEventType() {
        return eventType;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public String getGender() {
        return gender;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
