package com.example.demo.service.dashboard;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.entity.User;

public interface DashboardService {

    DashboardStatsDto getDashboardStats(User user);

}
