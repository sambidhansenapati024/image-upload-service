package com.example.demo.service;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.dto.ProfileUpdateResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {

    ProfileResponse getMyProfile();
    ProfileUpdateResponse updateMyProfile(ProfileUpdateRequest request);
    ProfileUpdateResponse uploadProfileImage(MultipartFile file) throws IOException;

}