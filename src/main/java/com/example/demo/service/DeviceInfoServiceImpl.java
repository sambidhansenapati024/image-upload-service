package com.example.demo.service;

import com.example.demo.dto.DeviceInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;

@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {

    private final Parser parser = new Parser();

    @Override
    public DeviceInfo extract(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        Client client = parser.parse(userAgent);

        String browser = client.userAgent.family;

        String operatingSystem = client.os.family;

        String device = client.device.family;

        switch (device.toLowerCase()) {
            case "other":
                device = "Desktop";
                break;
            case "iphone":
                device = "iPhone";
                break;
            case "ipad":
                device = "iPad";
                break;
            default:
                break;
        }

        String ipAddress = extractClientIp(request);

        return new DeviceInfo(
                browser,
                operatingSystem,
                device,
                ipAddress
        );

    }

    private String extractClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();

    }

}
