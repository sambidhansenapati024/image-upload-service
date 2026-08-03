package com.example.demo.mapper;

import com.example.demo.dto.ActivityLogResponseDto;
import com.example.demo.entity.ActivityLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityLogMapper {

    public ActivityLogResponseDto toDto(ActivityLog activityLog) {


        if (activityLog == null) {
            return null;
        }

        ActivityLogResponseDto dto = new ActivityLogResponseDto();

        dto.setId(activityLog.getId());
        dto.setActionType(activityLog.getActionType());
        dto.setMessage(activityLog.getMessage());
        dto.setReferenceId(activityLog.getReferenceId());
        dto.setRead(activityLog.isRead());
        dto.setCreatedAt(activityLog.getCreatedAt());

        return dto;
    }

    public List<ActivityLogResponseDto> toDtoList(List<ActivityLog> activityLogs) {
        return activityLogs.stream()
                .map(this::toDto)
                .toList();
    }
}