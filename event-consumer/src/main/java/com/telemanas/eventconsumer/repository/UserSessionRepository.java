package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telemanas.eventconsumer.model.UserSession;

// Repository for persisting user session data to the database.
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
}
