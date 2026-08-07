package com.example.demo.controller;

import com.example.demo.dto.OtpResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResendOtpRequest;
import com.example.demo.dto.SendOtpRequest;
import com.example.demo.service.otp.OtpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                otpService.sendOtp(request)
        );

    }

    @PostMapping("/resend-otp")
    public ResponseEntity<OtpResponse> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        return ResponseEntity.ok(
                otpService.resendOtp(request)
        );
    }
}
