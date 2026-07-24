package com.example.demo.controller;


import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.dto.ProfileUpdateResponse;
import com.example.demo.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/me")
    public ProfileResponse getMyProfile(){
        return profileService.getMyProfile();
    }

    @PutMapping("/update")
    public ProfileUpdateResponse updateMyProfile(
            @Valid @RequestBody ProfileUpdateRequest request) {

        return profileService.updateMyProfile(request);
    }

    @PostMapping("/image")
    public ResponseEntity<ProfileUpdateResponse> uploadProfileImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(
                profileService.uploadProfileImage(file)
        );
    }


}
