package com.example.demo.controller;

import com.example.demo.dto.ActivityLogResponseDto;
import com.example.demo.entity.User;
import com.example.demo.mapper.ActivityLogMapper;
import com.example.demo.service.pushNtification.ActivityLogService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final ActivityLogService activityLogService;
    private final ActivityLogMapper activityLogMapper;

    public NotificationController(ActivityLogService activityLogService,
                                  ActivityLogMapper activityLogMapper) {
        this.activityLogService = activityLogService;
        this.activityLogMapper = activityLogMapper;
    }

    @GetMapping
    public List<ActivityLogResponseDto> getNotifications(Authentication authentication) {

        return activityLogMapper.toDtoList(
                activityLogService.getActivities(authentication.getName())
        );
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(Authentication authentication) {


        return activityLogService.getUnreadCount(authentication.getName());
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id,
                           Authentication authentication) {

        activityLogService.markAsRead(
                id,
                authentication.getName()
        );
    }

    @PutMapping("/read-all")
    public void markAllAsRead(Authentication authentication) {

        activityLogService.markAllAsRead(
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id,
                                   Authentication authentication) {

        activityLogService.deleteActivity(
                id,
                authentication.getName()
        );

    }

    @DeleteMapping
    public void deleteAllNotifications(Authentication authentication) {

        activityLogService.deleteAllActivities(
                authentication.getName()
        );

    }
}
