package com.example.demo.repo;

import com.example.demo.entity.ActivityLog;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserOrderByCreatedAtDesc(User user);

    List<ActivityLog> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    long countByUserAndIsReadFalse(User user);

    void deleteByIdAndUser(Long id, User user);

    void deleteByUser(User user);
}
