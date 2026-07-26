package com.example.demo.repo;

import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository
        extends JpaRepository<UserSession, Long> {

    List<UserSession> findByUserAndActiveTrue(User user);

    Optional<UserSession> findBySessionId(UUID sessionId);

    List<UserSession> findByUserAndActiveTrueOrderByLoginTimeDesc(User user);

}
