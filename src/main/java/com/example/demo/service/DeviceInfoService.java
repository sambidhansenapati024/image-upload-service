package com.example.demo.service;

import com.example.demo.dto.DeviceInfo;
import jakarta.servlet.http.HttpServletRequest;

public interface DeviceInfoService {

    DeviceInfo extract(HttpServletRequest request);

}
