package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telemanas.eventconsumer.model.AutoCall;

// Repository for persisting auto call data to the database.
@Repository
public interface AutoCallRepository extends JpaRepository<AutoCall, String> {
}
