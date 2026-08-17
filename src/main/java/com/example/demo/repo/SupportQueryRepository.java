package com.example.demo.repo;

import com.example.demo.entity.SupportQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupportQueryRepository
        extends JpaRepository<SupportQuery, Long> {

    Optional<SupportQuery> findByQueryId(Long queryId);

    boolean existsByQueryId(Long queryId);

    Optional<SupportQuery> findByRequestId(UUID requestId);

    List<SupportQuery> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<SupportQuery> findByQueryIdAndUserId(
            Long queryId,
            Long userId
    );

}
