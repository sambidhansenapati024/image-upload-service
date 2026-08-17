package com.example.demo.repo;

import com.example.demo.entity.SupportQueryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportQueryStatusHistoryRepository
        extends JpaRepository<SupportQueryStatusHistory, Long> {

    List<SupportQueryStatusHistory> findBySupportQueryIdOrderByChangedAtAsc(
            Long supportQueryId
    );


}
