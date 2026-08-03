package com.example.demo.service.pushNtification;

import com.example.demo.entity.ActivityLog;
import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;
import com.example.demo.enums.ActionType;

import java.util.List;

public interface ActivityLogService {

    ActivityLog logActivity(String email,
                            ActionType actionType,
                            String message,
                            Long referenceId);

    List<ActivityLog> getActivities(String email);

    List<ActivityLog> getUnreadActivities(String email);

    long getUnreadCount(String email);

    void markAsRead(Long activityId, String email);

    void markAllAsRead(String email);

    void deleteActivity(Long activityId, String email);

    void deleteAllActivities(String email);

    void logNewLogin(String email, UserSession session);
}