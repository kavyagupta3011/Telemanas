package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
}
