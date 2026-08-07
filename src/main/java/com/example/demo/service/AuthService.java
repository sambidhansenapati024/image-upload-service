package com.example.demo.service;

import com.example.demo.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    public RegisterResponse register(
            CompleteRegistrationRequest request);

    LoginResponse login(LoginRequest request,  HttpServletRequest httpRequest);

}