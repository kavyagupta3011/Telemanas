package com.telemanas.eventconsumer.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.telemanas.eventconsumer.model.UserDisposition;

public interface UserDispositionRepository extends JpaRepository<UserDisposition, String> {
    
}
