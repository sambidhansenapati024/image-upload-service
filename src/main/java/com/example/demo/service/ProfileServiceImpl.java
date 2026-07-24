package com.example.demo.service;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.ProfileUpdateRequest;
import com.example.demo.dto.ProfileUpdateResponse;
import com.example.demo.dto.storage.StorageUploadResponse;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repo.ProfileRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.storage.StorageService;
import com.example.demo.util.MessageConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final StorageService storageService;

    public ProfileServiceImpl(UserRepository userRepository,
                              ProfileRepository profileRepository,StorageService storageService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.storageService = storageService;
    }


    @Override
    public ProfileResponse getMyProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        ProfileResponse response = new ProfileResponse();

        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());

        response.setPhone(profile.getPhone());
        response.setCountry(profile.getCountry());
        response.setTimezone(profile.getTimezone());
        response.setBio(profile.getBio());
        response.setProfileImageUrl(profile.getProfileImageUrl());
        response.setProfileCompleted(profile.isProfileCompleted());

        response.setJoinedOn(user.getCreatedAt());

        return response;
    }

    @Override
    public ProfileUpdateResponse updateMyProfile(ProfileUpdateRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setPhone(request.getPhone());
        profile.setCountry(request.getCountry());
        profile.setTimezone(request.getTimezone());
        profile.setBio(request.getBio());

        profile.setProfileCompleted(true);

        profileRepository.save(profile);

        ProfileResponse response = new ProfileResponse();

        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());

        response.setPhone(profile.getPhone());
        response.setCountry(profile.getCountry());
        response.setTimezone(profile.getTimezone());
        response.setBio(profile.getBio());
        response.setProfileImageUrl(profile.getProfileImageUrl());
        response.setProfileCompleted(profile.isProfileCompleted());

        response.setJoinedOn(user.getCreatedAt());

        return new ProfileUpdateResponse(
                true,"Profile Updated Sucessfully"
        );
    }

    @Override
    public ProfileUpdateResponse uploadProfileImage(MultipartFile file) throws IOException {

        User user = getLoggedInUser();

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        StorageUploadResponse uploadResponse = storageService.upload(file);

        profile.setProfileImageUrl(uploadResponse.getUrl());

        profileRepository.save(profile);

        return new ProfileUpdateResponse(
                true,
                "Profile image updated successfully."
        );
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));
    }


}
