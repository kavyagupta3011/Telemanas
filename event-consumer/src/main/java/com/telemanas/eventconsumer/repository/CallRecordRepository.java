package com.telemanas.eventconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telemanas.eventconsumer.model.CallRecord;

// Repository for persisting call record data to the database.
@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, String> {
}