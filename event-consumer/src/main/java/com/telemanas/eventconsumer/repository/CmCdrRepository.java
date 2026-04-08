package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.CmCdr;

public interface CmCdrRepository extends JpaRepository<CmCdr, String> {
}
