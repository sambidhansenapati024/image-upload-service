package com.example.demo.service.pushNtification;

import com.example.demo.dto.ActivityLogResponseDto;
import com.example.demo.entity.ActivityLog;
import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;
import com.example.demo.enums.ActionType;
import com.example.demo.mapper.ActivityLogMapper;
import com.example.demo.repo.ActivityLogRepository;
import com.example.demo.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {


    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final ActivityLogMapper activityLogMapper;
    private final NotificationWebSocketService notificationWebSocketService;
    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository,
                                  UserRepository userRepository,
                                  ActivityLogMapper activityLogMapper,
                                  NotificationWebSocketService notificationWebSocketService) {

        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.activityLogMapper = activityLogMapper;
        this.notificationWebSocketService = notificationWebSocketService;
    }

    @Override
    public ActivityLog logActivity(String email,
                                   ActionType actionType,
                                   String message,
                                   Long referenceId) {

        ActivityLog activityLog = new ActivityLog();
        User user = getUser(email);

        activityLog.setUser(user);
        activityLog.setActionType(actionType);
        activityLog.setMessage(message);
        activityLog.setReferenceId(referenceId);

        ActivityLog savedActivity = activityLogRepository.save(activityLog);

        ActivityLogResponseDto responseDto =
                activityLogMapper.toDto(savedActivity);

        notificationWebSocketService.sendToUser(
                user.getEmail(),
                responseDto
        );

        return savedActivity;    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivities(String email) {
        User user = getUser(email);
        return activityLogRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getUnreadActivities(String email)  {
        User user = getUser(email);
        return activityLogRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        User user = getUser(email);
        return activityLogRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    public void markAsRead(Long activityId, String email) {

        User user = getUser(email);
        ActivityLog activityLog = activityLogRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        if (!activityLog.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not allowed to modify this activity.");
        }

        activityLog.setRead(true);

        activityLogRepository.save(activityLog);
    }

    @Override
    public void markAllAsRead(String email) {
        User user = getUser(email);
        List<ActivityLog> activities =
                activityLogRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);

        activities.forEach(activity -> activity.setRead(true));

        activityLogRepository.saveAll(activities);
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteActivity(Long activityId, String email) {

        User user = getUser(email);

        activityLogRepository.deleteByIdAndUser(activityId, user);

    }

    @Override
    public void deleteAllActivities(String email) {

        User user = getUser(email);

        activityLogRepository.deleteByUser(user);

    }

    public void logNewLogin(String email, UserSession session) {

        logActivity(
                email,
                ActionType.NEW_LOGIN,
                String.format(
                        "New login detected from %s on %s (%s). IP: %s",
                        session.getBrowser(),
                        session.getOperatingSystem(),
                        session.getDevice(),
                        session.getIpAddress()
                ),
                null
        );
    }
}