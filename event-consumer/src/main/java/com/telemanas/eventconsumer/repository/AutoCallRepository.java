package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.AutoCall;

public interface AutoCallRepository extends JpaRepository<AutoCall, String> {
}
