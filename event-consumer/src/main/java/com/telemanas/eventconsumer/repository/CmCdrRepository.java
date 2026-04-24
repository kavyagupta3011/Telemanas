package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telemanas.eventconsumer.model.CmCdr;

// Repository for persisting CM CDR data to the database.
@Repository
public interface CmCdrRepository extends JpaRepository<CmCdr, String> {
}
