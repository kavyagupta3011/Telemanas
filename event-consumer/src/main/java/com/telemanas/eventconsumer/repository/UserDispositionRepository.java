package com.telemanas.eventconsumer.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telemanas.eventconsumer.model.UserDisposition;


// Repository for persisting user disposition data to the database.
@Repository
public interface UserDispositionRepository extends JpaRepository<UserDisposition, String> {
}
