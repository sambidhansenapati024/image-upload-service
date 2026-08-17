package com.example.demo.service.supportQuerry;

import com.example.demo.dto.*;
import com.example.demo.entity.SupportQuery;
import com.example.demo.entity.SupportQueryStatusHistory;
import com.example.demo.entity.User;
import com.example.demo.enums.SupportQueryHistoryStatus;
import com.example.demo.enums.SupportQueryStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedOperationException;
import com.example.demo.repo.SupportQueryRepository;
import com.example.demo.repo.SupportQueryStatusHistoryRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.reids.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.event.SupportQueryCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SupportQueryServiceImpl
        implements SupportQueryService {

    private static final int MAX_QUERIES = 5;

    private static final long RATE_LIMIT_WINDOW_SECONDS =
            10 * 60;

    private static final String RATE_LIMIT_PREFIX =
            "support:query:rate:";

    @Value("${cloud.admin.email}")
    private String adminEmail;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SupportQueryStatusHistoryRepository statusHistoryRepository;

    private final SupportQueryRepository supportQueryRepository;

    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;


    public SupportQueryServiceImpl(
            SupportQueryRepository supportQueryRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {

        this.supportQueryRepository = supportQueryRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Transactional
    public SupportQueryResponse createQuery(
            SupportQueryRequest request) {

        /*
         * Get currently authenticated user
         */

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (authentication == null
                || !authentication.isAuthenticated()) {

            return new SupportQueryResponse(
                    false,
                    "User is not authenticated.",
                    null
            );

        }

        String email =
                authentication.getName();


        /*
         * Find user from database
         */

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);


        if (user == null) {

            return new SupportQueryResponse(
                    false,
                    "User not found.",
                    null
            );

        }


        Optional<SupportQuery> existingQuery =
                supportQueryRepository.findByRequestId(
                        request.getRequestId()
                );

        if (existingQuery.isPresent()) {

            SupportQuery existing =
                    existingQuery.get();

            if (!existing.getUser().getId()
                    .equals(user.getId())) {

                return new SupportQueryResponse(
                        false,
                        "Invalid request.",
                        null
                );
            }

            return new SupportQueryResponse(
                    true,
                    "Your query has already been submitted.",
                    existing.getQueryId()
            );
        }

        String rateLimitKey =
                RATE_LIMIT_PREFIX
                        + user.getId();

        long currentCount =
                redisService.incrementWithExpiry(
                        rateLimitKey,
                        RATE_LIMIT_WINDOW_SECONDS
                );
        if (currentCount > MAX_QUERIES) {

            return new SupportQueryResponse(
                    false,
                    "You have reached the support query limit. "
                            + "Please try again later.",
                    null
            );
        }



        /*
         * Generate public Query ID
         */

        Long queryId =
                generateQueryId();


        /*
         * Create SupportQuery entity
         */

        SupportQuery supportQuery =
                new SupportQuery();

        supportQuery.setQueryId(queryId);

        supportQuery.setUser(user);

        supportQuery.setQuery(
                request.getQuery().trim()
        );

        supportQuery.setStatus(
                SupportQueryStatus.OPEN
        );

        supportQuery.setRequestId(
                request.getRequestId()
        );

        supportQuery.setQueryType(
                request.getQueryType()
        );


        /*
         * Save
         */

        supportQueryRepository.save(
                supportQuery
        );

        createStatusHistory(
                supportQuery,
                SupportQueryHistoryStatus.RAISED
        );

        createStatusHistory(
                supportQuery,
                SupportQueryHistoryStatus.RECEIVED
        );

        eventPublisher.publishEvent(
                new SupportQueryCreatedEvent(
                        supportQuery
                )
        );

        /*
         * Return response
         */

        return new SupportQueryResponse(
                true,
                "Your query has been submitted successfully.",
                queryId
        );

    }


    @Override
    @Transactional(readOnly = true)
    public List<SupportQueryListResponse> getMyQueries() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new UnauthorizedOperationException(
                    "User is not authenticated."
            );
        }

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                )
                        );

        return supportQueryRepository
                .findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(query ->
                        new SupportQueryListResponse(
                                query.getQueryId(),
                                query.getQueryType(),
                                query.getStatus(),
                                query.getQuery(),
                                query.getCreatedAt(),
                                query.getUpdatedAt()
                        )
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupportQueryDetailsResponse getMyQueryDetails(
            Long queryId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new UnauthorizedOperationException(
                    "User is not authenticated."
            );
        }

        String email = authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                )
                        );

        SupportQuery supportQuery =
                supportQueryRepository
                        .findByQueryIdAndUserId(
                                queryId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Support query not found."
                                )
                        );

        List<SupportQueryTimelineResponse> timeline =
                statusHistoryRepository
                        .findBySupportQueryIdOrderByChangedAtAsc(
                                supportQuery.getId()
                        )
                        .stream()
                        .map(history ->
                                new SupportQueryTimelineResponse(
                                        history.getStatus(),
                                        history.getChangedAt()
                                )
                        )
                        .toList();

        return new SupportQueryDetailsResponse(
                supportQuery.getQueryId(),
                supportQuery.getQueryType(),
                supportQuery.getStatus(),
                supportQuery.getQuery(),
                supportQuery.getCreatedAt(),
                supportQuery.getUpdatedAt(),
                timeline
        );
    }


    private Long generateQueryId() {

        return ThreadLocalRandom.current()
                .nextLong(
                        10_000_000L,
                        100_000_000L
                );
    }

    private void createStatusHistory(
            SupportQuery supportQuery,
            SupportQueryHistoryStatus status) {

        SupportQueryStatusHistory history =
                new SupportQueryStatusHistory();

        history.setSupportQuery(
                supportQuery
        );

        history.setStatus(
                status
        );

        history.setChangedAt(
                java.time.LocalDateTime.now()
        );

        statusHistoryRepository.save(
                history
        );
    }

}
